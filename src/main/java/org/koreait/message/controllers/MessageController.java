package org.koreait.message.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 쪽지
 *
 * 쪽지 수정은 X
 * CRD 작업
 * 
 */
@Controller
@ApplyErrorPage
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final Utils utils;

    /**
     * 쪽지 보내기 양식
     * @return
     */
    @GetMapping
    public String form() {

        return utils.tpl("message/form");
    }

    /**
     * 쪽지 작성
     *
     * @return
     */
    @PostMapping
    public String process() {

        // 전송 후 쪽지 목록으로
        return "redirect:/message/list";
    }

    /**
     * 쪽지함
     * mode 값에 따라 수신/발신 쪽지함 구분
     *
     * @return
     */
    @GetMapping("/list")
    public String list() {

        return utils.tpl("message/list");
    }

    /**
     * 쪽지 개별 조회
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq) {

        return utils.tpl("message/view");
    }
}