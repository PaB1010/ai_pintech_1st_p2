package org.koreait.board.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.entities.BoardData;
import org.koreait.board.entities.CommentData;
import org.koreait.board.exceptions.GuestPasswordCheckException;
import org.koreait.board.services.*;
import org.koreait.board.services.comment.CommentDeleteService;
import org.koreait.board.services.comment.CommentInfoService;
import org.koreait.board.services.comment.CommentUpdateService;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.board.validators.BoardValidator;
import org.koreait.board.validators.CommentValidator;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.exceptions.scripts.AlertException;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.libs.MemberUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final BoardDeleteService boardDeleteService;

    private final BoardAuthService boardAuthService;

    private final CodeValueService codeValueService;

    private final CommentUpdateService commentUpdateService;

    private final CommentInfoService commentInfoService;

    private final CommentDeleteService commentDeleteService;

    private final CommentValidator commentValidator;

    private final HttpServletRequest request;

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
    public String view(@PathVariable("seq") Long seq, Model model, @ModelAttribute RequestComment form) {

        commonProcess(seq, "view", model);

        // 조회수 업데이트
        long viewCount = boardViewUpdateService.process(seq);

        BoardData data = (BoardData) model.getAttribute("boardData");

        data.setViewCount(viewCount);

        Board board = data.getBoard();

        // 게시글 조회 페이지 하단에 게시글 목록 출력 사용할 경우
        if (board.isListUnderView()) {

            ListData<BoardData> listData = boardInfoService.getList(board.getBid(), new BoardSearch());

            model.addAttribute("items", listData.getItems());
            model.addAttribute("pagination", listData.getPagination());
        }

        // 댓글 사용하는 경우
        if (board.isUseComment()) {

            if (memberUtil.isLogin()) {

                form.setCommenter(memberUtil.getMember().getNickName());
            }

            form.setMode("write");
            form.setTarget("ifrmProcess");
            form.setBoardDataSeq(seq);

            List<CommentData> comments = commentInfoService.getList(seq);

            model.addAttribute("comments", comments);
        }


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
    public String delete(@PathVariable("seq") Long seq, Model model, @SessionAttribute("commonValue") CommonValue commonValue) {

        commonProcess(seq, "delete", model);

        Board board = commonValue.getBoard();

        // 게시글에 댓글이 있으면 삭제 불가
        boardValidator.checkDelete(seq);

        boardDeleteService.delete(seq);

        return "redirect:/board/list/" + board.getBid();
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

        if (mode.equals("edit")) commonProcess(form.getSeq(), mode, model);

        else commonProcess(form.getBid(), mode, model);

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
     * 댓글 등록 & 수정
     *
     * @param form
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/comment")
    public String comment(@Valid RequestComment form, Errors errors, Model model) {
        String mode = form.getMode();

        mode = StringUtils.hasText(mode) ? mode : "write";

        if (mode.equals("edit")) {

            commonProcess(form.getSeq(), "comment", model);
        }

        commentValidator.validate(form, errors);

        if (errors.hasErrors()) {
            if (!mode.equals("edit")) { // 댓글 등록시에는 alert 메세지로 검증 실패를 알린다.
                FieldError err = errors.getFieldErrors().get(0);
                String code = err.getCode();
                String field = err.getField();
                throw new AlertException(utils.getMessage(code + ".requestComment." + field));
            }

            return utils.tpl("board/comment"); // 수정
        }

        // 댓글 등록/수정 서비스
        CommentData item = commentUpdateService.save(form);

        String redirectUrl = String.format("/board/view/%d#comment-%d", form.getBoardDataSeq(), item.getSeq());
        if (mode.equals("edit")) {
            return "redirect:" + redirectUrl;
        } else {

            redirectUrl = request.getContextPath() + redirectUrl;
            
            // 새로고침후 현재 댓글 위치로 이동
            String script = String.format("parent.location.reload(); parent.addEventListener('DOMContentLoaded', function() { parent.location.replace('%s'); });", redirectUrl);

            model.addAttribute("script", script);

            return "common/_execute_script";
        }
    }

    /**
     * 댓글 수정
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/comment/edit/{seq}")
    public String commentEdit(@PathVariable("seq") Long seq, Model model) {

        commonProcess(seq, "comment", model);

        RequestComment form = commentInfoService.getForm(seq);

        model.addAttribute("requestComment", form);

        return utils.tpl("board/comment");
    }

    /**
     * 댓글 삭제 처리
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/comment/delete/{seq}")
    public String commentDelete(@PathVariable("seq") Long seq, Model model) {

        commonProcess(seq, "comment", model);

        BoardData item = commentDeleteService.delete(seq);

        return "redirect:/board/view/" + item.getSeq();
    }

    /**
     * 비회원 비밀번호 처리
     *
     * @return
     */
    @ExceptionHandler(GuestPasswordCheckException.class)
    public String guestPassword(Model model) {

        SiteConfig config = Objects.requireNonNullElseGet(codeValueService.get("siteConfig", SiteConfig.class), SiteConfig::new);

        model.addAttribute("siteConfig", config);

        return utils.tpl("board/password");
    }

    /**
     * 비회원 비밀번호 검증
     *
     * @param password
     * @param session
     * @param model
     * @return
     */
    @PostMapping("/password")
    public String validateGuestPassword(@RequestParam(name = "password", required = false) String password, HttpSession session, Model model) {

        // 히든 프레임 예정
        if (!StringUtils.hasText(password)) throw new AlertException(utils.getMessage("NotBlank.password"));

        /* 비회원 게시글 비밀번호 검증 S */
        Long seq = (Long) session.getAttribute("seq");

        if (seq != null && seq > 0L) {

            if (!boardValidator.checkGuestPassword(password, seq)) {

                throw new AlertException(utils.getMessage("Mismatch.password"));
            }


            // 비회원 비밀번호 검증 성공시 Session (board_게시글번호) 추가
            session.setAttribute("board_" + seq, true);
        }
        /* 비회원 게시글 비밀번호 검증 E */

        /* 비회원 댓글 비밀번호 검증 S */
        Long cSeq = (Long) session.getAttribute("cSeq");

        if (cSeq != null && cSeq > 0L) {

            if (!commentValidator.checkGuestPassword(password, cSeq)) {

                throw new AlertException(utils.getMessage("Mismatch.password"));
            }

            // 비회원 댓글 비밀번호 검증 성공 comment_댓글번호
            session.setAttribute("comment_" + cSeq, true);
        }

        /* 비회원 댓글 비밀번호 검증 E */

        // 비회원 비밀번호 인증 완료된 경우 새로 고침
        model.addAttribute("script", "parent.location.reload();");

        return "common/_execute_script";
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

        // 게시판 권한 체크
        if (!List.of("edit", "delete", "comment").contains(mode)) {

            boardAuthService.check(mode, bid);
        }

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
        model.addAttribute("mode", mode);
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

        BoardData item = null;

        CommentData comment = null;

        if (mode.equals("comment")) { // 댓글 수정 & 삭제

            comment = commentInfoService.get(seq);

            item = comment.getData();

        } else {

            item = boardInfoService.get(seq);
        }
        Board board = item.getBoard();

        String bid = board.getBid();

        // 게시판 권한 체크
        boardAuthService.check(mode, seq);

        // 게시글 제목 - 게시판명
        String pageTitle = String.format("%s - %s", item.getSubject(), board.getName());

        commonProcess(bid, mode, model);

        // 게시판 설정 추가 처리
        CommonValue commonValue = commonValue();

        commonValue.setBoard(board);
        commonValue.setData(item);
        commonValue.setComment(comment);

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

        private CommentData comment;
    }
}