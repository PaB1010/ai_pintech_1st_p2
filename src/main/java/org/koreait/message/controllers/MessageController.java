package org.koreait.message.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.message.validators.MessageValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private final MessageValidator messageValidator;

    @ModelAttribute("addCss")
    public List<String> addCss() {

        return List.of("message/style");
    }

    /**
     * 쪽지 보내기 양식
     * @return
     */
    @GetMapping
    public String form(@ModelAttribute RequestMessage form, Model model) {

        commonProcess("send", model);
        
        // 파일 첨부시 Random gid 발급
        form.setGid(UUID.randomUUID().toString());

        model.addAttribute("send", model);

        return utils.tpl("message/form");
    }

    /**
     * 쪽지 작성
     *
     * @return
     */
    @PostMapping
    public String process(@Valid RequestMessage form, Errors errors, Model model) {

        commonProcess("send", model);

        messageValidator.validate(form, errors);

        // 검증 실패시 다시 양식으로
        if (errors.hasErrors()) return utils.tpl("message/form");

        model.addAttribute("send", model);

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
    public String list(Model model) {

        commonProcess("list", model);

        model.addAttribute("list", model);

        return utils.tpl("message/list");
    }

    /**
     * 쪽지 개별 조회
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {

        commonProcess("view", model);

        model.addAttribute("view", model);

        return utils.tpl("message/view");
    }


    /**
     * 쪽지 개별 & 다수 삭제
     *
     * @param seq
     * @return
     */
    @DeleteMapping
    public String delete(@RequestParam(name = "seq", required = false) List<String> seq) {

        return "redirect:/message/list";
    }

    /**
     * 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "list";

        String pageTitle = "";

        // File Upload 관련 공통 JavaScript 추가 예정
        List<String> addCommonScript = new ArrayList<>();

        List<String> addScript = new ArrayList<>();

        List<String> addCss = new ArrayList<>();

        if (mode.equals("send")) {

            pageTitle = utils.getMessage("쪽지_보내기");

            addCommonScript.add("fileManager");

            addCommonScript.add("ckeditor5/ckeditor");

            addScript.add("message/send");


        } else if (mode.equals("list")) {

            pageTitle = utils.getMessage("쪽지함");

            model.addAttribute("list", model);

        } else if (mode.equals("view")) {

            pageTitle = utils.getMessage("쪽지_보기");

            model.addAttribute("view", model);

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }
}