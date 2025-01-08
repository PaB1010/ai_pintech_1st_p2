package org.koreait.board.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.koreait.board.entities.Board;
import org.koreait.board.services.configs.BoardConfigInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Controller
@ApplyErrorPage
@RequestMapping("/board")
@RequiredArgsConstructor
@SessionAttributes({"commonValue"})
public class BoardController {

    private final Utils utils;

    private final BoardConfigInfoService configInfoService;

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
    public String list(@PathVariable("bid") String bid, Model model) {

        commonProcess(bid, "list", model);

        return utils.tpl("board/list");
    }

    /**
     * 게시글 조회
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {

        commonProcess(seq, "view", model);

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
    public String write(@PathVariable("bid") String bid, Model model) {

        commonProcess(bid, "add", model);

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
    public String edit(@PathVariable("seq") Long seq, Model model) {

        commonProcess(seq, "edit", model);

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
     * 게시글 등록 & 수정 처리
     * 
     * 처리 성공시 게시글 목록 || 게시글 조회로 이동
     *
     * @param mode
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save(@SessionAttribute("commonValue") CommonValue commonValue, String mode, Model model) {

        Board board = commonValue.getBoard();

        String redirectUrl = String.format("board/%s/edit", board.getLocationAfterWriting()
                .equals("view") ? "view/..." : "list/" + board.getBid());

        return "redirect:" + redirectUrl;
    }

    /**
     * 공통 처리 부분
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

        if (mode.equals("add") || mode.equals("edit")) {
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
            addScript.add(String.format("board/%s/common", board.getSkin()));
        }

        CommonValue commonValue = commonValue();
        commonValue.setBoard(board);

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("board", board);
        model.addAttribute("commonValue", commonValue);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
    }

    /**
     * seq 로 게시글 조회후 ManyToOne Mapping 된 게시판 설정 가져와 bid 대체
     *
     * @param seq
     * @param mode
     * @param model
     */
    private void commonProcess(Long seq, String mode, Model model) {

        String bid = null;

        commonProcess(bid, mode, model);
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

        // 추후 게시글 데이터 예정
    }
}