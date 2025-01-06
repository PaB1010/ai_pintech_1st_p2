package org.koreait.message.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.message.entities.Message;
import org.koreait.message.services.MessageDeleteService;
import org.koreait.message.services.MessageInfoService;
import org.koreait.message.services.MessageSendService;
import org.koreait.message.services.MessageStatusService;
import org.koreait.message.validators.MessageValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private final FileInfoService fileInfoService;

    private final MessageSendService sendService;

    private final MessageInfoService infoService;

    private final MessageStatusService statusService;

    private final MessageDeleteService deleteService;

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
        if (errors.hasErrors()) {
            // 업로드한 파일 목록 form 에 추가
            String gid = form.getGid();

            // FileStatus 전체가 나오도록 ALL
            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));

            form.setAttachFiles(fileInfoService.getList(gid, "attach", FileStatus.ALL));

            return utils.tpl("message/form");
        }

        sendService.process(form);

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
    public String list(@ModelAttribute MessageSearch search, Model model) {

        commonProcess("list", model);

        String mode = search.getMode();

        search.setMode(StringUtils.hasText(mode) ? mode : "receive");

        ListData<Message> data = infoService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("message/list");
    }

    /**
     * 쪽지 개별 조회
     *
     * @param seq
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model, HttpServletRequest request) {

        commonProcess("view", model);

        Message item = infoService.get(seq);

        model.addAttribute("item", item);

        // 미열람 -> 열람 변경
        statusService.change(seq);

        // 요청 header 에서 referer (직전 유입 URL)
        String referer = Objects.requireNonNullElse(request.getHeader("referer"), "");

        model.addAttribute("mode", referer.contains("mode=send") ? "send" : "receive");

        return utils.tpl("message/view");
    }


    /**
     * 쪽지 개별 삭제
     *
     * @param seq
     * @return
     */
    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, @RequestParam(name = "mode", defaultValue = "receive") String mode) {

        deleteService.process(seq, mode);

        return "redirect:/message/list";
    }

//    // list 에서 쪽지 다수 삭제
//    @GetMapping("/delete/{seq}")
//    public String delete(@RequestParam(name = "seq", required = false) List<String> seq) {
//
//        return "redirect:/message/list";
//    }

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