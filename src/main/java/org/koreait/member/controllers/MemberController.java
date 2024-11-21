package org.koreait.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.global.libs.Utils;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes("requestAgree")
public class MemberController {

    // 공통 기능 의존 주입
    private final Utils utils;
    
    // 회원 가입 검증 의존 주입
    private final JoinValidator joinValidator;

    // 회원 가입 처리 의존 주입
    private final MemberUpdateService updateService;

    @ModelAttribute("requestAgree")
    public RequestAgree requestAgree() {
        return new RequestAgree();
    }

    /* 회원 페이지 CSS */

    @ModelAttribute("addCss")
    public List<String> addCss() {

        return List.of("member/style");
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Model model) {

        // 로그인 페이지 공통 처리
        commonProcess("login", model);

        // uilts.tpl = PC / Mobile 분리
        return utils.tpl("member/login");
    }

    @PostMapping("/login")
    public String loginPs(@Valid RequestLogin form, Errors erros, Model model) {

        // 로그인 페이지 공통 처리
        commonProcess("login", model);

        if (erros.hasErrors()) {

            // uilts.tpl = PC / Mobile 분리
            return utils.tpl("member/login");
        }

        // 로그인 처리

        /**
         *  로그인 완료 후 페이지 이동
         *  1) redirectUrl 값이 전달 된 경우는 해당 경로로 이동
         *  2) 없는 경우 main page로 이동
         */

        String redirectUrl = form.getRedirectUrl();

        // Stringutils = ThymeLeaf말고 반드시 Springframework
        redirectUrl = StringUtils.hasText(redirectUrl) ? redirectUrl : "/";

        // 이전 page OR main page 이동
        return "redirect:" + redirectUrl;
    }

    /**
     * 회원가입 약관 동의
     *
     * @return
     */
    @GetMapping("/agree")
    public String joinAgree(Model model) {

        // 회원 가입 공통 처리
        commonProcess("agree", model);

        return utils.tpl("member/agree");
    }

    /**
     * 회원가입 양식 페이지
     * - 필수 약관 동의 여부 검증
     *
     * @return
     */
    @PostMapping("/join")
    public String join(RequestAgree agree, Errors errors, @ModelAttribute RequestJoin form, Model model) {
        
        // 회원 가입 공통 처리
        commonProcess("join", model);

        joinValidator.validate(agree, errors);

        // log.info(form.toString());

        if (errors.hasErrors()) { // 약관 동의를 하지 않았다면 약관 동의 화면 출력

            return utils.tpl("member/agree");
        }

        return utils.tpl("member/join");
    }

    /**
     * 회원가입 처리
     *
     * @return
     */
    @PostMapping("/join_ps")
    public String joinPs(@SessionAttribute("requestAgree") RequestAgree agree, @Valid RequestJoin form, Errors errors, SessionStatus status, Model model) {

        // 회원 가입 공통 처리
        commonProcess("join", model);

        // 약관 동의 여부 체크
        joinValidator.validate(agree, errors);
        // 회원 가입 양식 검증
        joinValidator.validate(form, errors);

        if (errors.hasErrors()) { // 검증 실패시 회원가입 화면 출력

            return utils.tpl("member/join");
        }

        // 회원 가입 처리
        form.setRequiredTerms1(agree.isRequiredTerms1());
        form.setRequiredTerms2(agree.isRequiredTerms2());
        form.setRequiredTerms3(agree.isRequiredTerms3());
        form.setOptionalTerms(agree.getOptionalTerms());

        // 회원 가입 검증 완료시 처리
        updateService.process(form);

        // Model을 통해 수정(추가 & 변경)되는 것 방지
        status.setComplete();

        // 회원가입 처리 완료 후 -> Login page 이동
        return "redirect:/member/login";
    }

    /**
     *
     * 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "login";

        // 페이지 제목
        String pageTitle = null;;

        // 공통 JavaScript
        List<String> addCommonScript = new ArrayList<>();

        // Front쪽에 추가할 JavaScript
        List<String> addScript = new ArrayList<>();

        // 로그인 공통 처리
        if (mode.equals("login")) {

            pageTitle = utils.getMessage("로그인");

            // 회원 가입 공통 처리
        } else if (mode.equals("join")) {

            pageTitle = utils.getMessage("회원가입");

            // common_address.js
            addCommonScript.add("address");

            // front_member_join.js
            addScript.add("member/join");

        } else if (mode.equals("agree")) {

            pageTitle = utils.getMessage("약관동의");

            //약관 동의(agree) page에 최초 접근시 약관 선택 초기화 (Session 비움)
            model.addAttribute("requestAgree", requestAgree());
        }

        // Page 제목
        model.addAttribute("pageTitle", pageTitle);

        // 공통 Script
        model.addAttribute("addCommonScript", addCommonScript);

        // Front쪽에 추가할 Script
        model.addAttribute("addScript", addScript);
    }
}