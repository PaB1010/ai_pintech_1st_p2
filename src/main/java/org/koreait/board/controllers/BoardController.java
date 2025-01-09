package org.koreait.board.controllers;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.services.BoardInfoService;
import org.koreait.board.services.BoardUpdateService;
import org.koreait.board.services.BoardViewUpdateService;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.board.validators.BoardValidator;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.member.libs.MemberUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@ApplyErrorPage
@RequestMapping("/board")
@RequiredArgsConstructor
@SessionAttributes({"commonValue"})
public class BoardController {

    private final Utils utils;

    private final MemberUtil memberUtil;

    private final BoardConfigInfoService configInfoService;

    private final FileInfoService fileInfoService;

    private final BoardValidator boardValidator;
    
    private final BoardUpdateService boardUpdateService;

    private final BoardInfoService boardInfoService;

    private final BoardViewUpdateService boardViewUpdateService;

    /**
     * 사용자별 공통 데이터
     *
     * - 게시판 설정
     *
     * @return
     */
    @ModelAttribute("commonValue")
    public CommonValue commonValue() {

        return new CommonValue();
    }

    /**
     * 게시글 목록
     *
     * @param bid
     * @param model
     * @return
     */
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, BoardSearch search, Model model) {

        commonProcess(bid, "list", model);

        ListData<BoardData> data = boardInfoService.getList(bid, search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("board/list");
    }

    /**
     * 게시글 조회
     *
     * pageTitle = 게시글 제목
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {

        commonProcess(seq, "view", model);

        // 조회수 업데이트
        long viewCount = boardViewUpdateService.process(seq);

        BoardData data = (BoardData) model.getAttribute("boardData");

        data.setViewCount(viewCount);

        return utils.tpl("board/view");
    }

    /**
     * 게시글 작성
     *
     * @param bid
     * @param model
     * @return
     */
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid, @ModelAttribute RequestBoard form, Model model) {

        commonProcess(bid, "write", model);

        form.setBid(bid);
        form.setGid(UUID.randomUUID().toString());

        if (memberUtil.isLogin()) {

            form.setPoster(memberUtil.getMember().getNickName());
        }

        return utils.tpl("board/write");
    }

    /**
     * 게시글 수정
     *
     * 비밀번호 인증 후 수정되도록 처리
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/edit/{seq}")
    public String edit(@PathVariable("seq") Long seq, Model model, @SessionAttribute("commonValue") CommonValue commonValue) {

        commonProcess(seq, "edit", model);

        // 커맨드 객체로 변환
        RequestBoard form = boardInfoService.getForm(commonValue.getData());

        model.addAttribute("requestBoard", form);

        return utils.tpl("board/edit");
    }

    /**
     * 게시글 삭제
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable Long seq, Model model, @SessionAttribute("commonValue") CommonValue commonValue) {

        commonProcess(seq, "delete", model);

        Board board = commonValue.getBoard();

        return utils.tpl("redirect:/board/list/" + board.getBid());
    }

    /**
     * 게시글 작성 & 수정 처리
     *
     * 처리 성공시 게시글 목록 || 게시글 조회로 이동
     *
     * @param form
     * @param errors
     * @param commonValue
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, @SessionAttribute("commonValue") CommonValue commonValue, Model model) {

        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

        commonProcess(form.getBid(), mode, model);

        boardValidator.validate(form, errors);

        if (errors.hasErrors()) {
            // 검증 실패시 업로드 파일 유지
            String gid = form.getGid();

            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));
            form.setAttachFiles(fileInfoService.getList(gid, "attach", FileStatus.ALL));

            return utils.tpl("board/" + mode);
        }

        BoardData data = boardUpdateService.process(form);

        Board board = commonValue.getBoard();

        // 글 작성 & 수정 처리 성공시 게시글 조회 || 게시글 목록 경로 이동
        String redirectUrl = String.format("/board/%s", board.getLocationAfterWriting()
                .equals("view") ? "view/" + data.getSeq() : "list/" + board.getBid());

        return "redirect:" + redirectUrl;
    }

    /**
     * 공통 처리 부분
     *
     * Base Method
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String bid, String mode, Model model) {

        Board board = configInfoService.get(bid);

        // 게시판명 - 게시글 목록, 게시글 작성
        String pageTitle = board.getName();

        mode = StringUtils.hasText(mode) ? mode : "list";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        // 게시판 공통 JS & CSS
        addScript.add("board/common");
        addCss.add("board/style");

        // 게시판 스킨별 JS & CSS
        addScript.add(String.format("board/%s/common", board.getSkin()));
        addCss.add(String.format("board/%s/style", board.getSkin()));

        if (mode.equals("write") || mode.equals("edit")) {
            // 게시글 작성 & 수정

            // 에디터 사용하는 경우
            if (board.isUseEditor()) {

                addCommonScript.add("ckeditor5/ckeditor");

            } else {

                // 에디터 사용하지 않는 경우 이미지 첨부 불가
                board.setUseEditorImage(false);
            }

            if (board.isUseAttachFile() || board.isUseEditorImage()) {
                // 파일 업로드가 필요한 설정일 경우

                addCommonScript.add("fileManager");
            }

            // 게시칸 스킨에 따라 처리
            // EX) 갤러리 스킨일 경우 게시글 썸네일 처리 기능 필요
            addScript.add(String.format("board/%s/form", board.getSkin()));
        }

        // 게시글 번호(seq)가 있는 mode 가 view || editor 일 경우를 배제
        if (!List.of("view", "edit").contains(mode)) {

            CommonValue commonValue = commonValue();
            commonValue.setBoard(board);

            model.addAttribute("commonValue", commonValue);
            model.addAttribute("pageTitle", pageTitle);
        }

        model.addAttribute("board", board);
        model.addAttribute("categories", board.getCategories());
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
    }

    /**
     * seq 로 게시글 조회후 ManyToOne Mapping 된 게시판 설정 가져와 bid 대체
     *
     * 게시글 조회 & 수정시 사용
     *
     * @param seq
     * @param mode
     * @param model
     */
    private void commonProcess(Long seq, String mode, Model model) {

        BoardData item = boardInfoService.get(seq);
        Board board = item.getBoard();

        // 게시글 제목 - 게시판명
        String pageTitle = String.format("%s - %s", item.getSubject(), board.getName());

        String bid = board.getBid();

        commonProcess(bid, mode, model);

        // 게시판 설정 추가 처리
        CommonValue commonValue = commonValue();

        commonValue.setBoard(board);
        commonValue.setData(item);

        model.addAttribute("commonValue", commonValue);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("boardData", item);
    }

    /**
     * 사용자별 공통 데이터
     *
     * SessionAttribute 용도
     * 내부 클래스
     */
    @Data
    static class CommonValue implements Serializable {

        private Board board;

        private BoardData data;
    }
}