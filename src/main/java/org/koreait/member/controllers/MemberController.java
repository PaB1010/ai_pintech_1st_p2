package org.koreait.member.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.MemberInfo;
import org.koreait.member.entities.Member;
import org.koreait.member.services.MemberDeleteService;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.member.social.constants.SocialChannel;
import org.koreait.member.social.entities.SocialConfig;
import org.koreait.member.social.services.KakaoLoginService;
import org.koreait.member.validators.JoinValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@ApplyErrorPage
@RequestMapping("/member")
@RequiredArgsConstructor
// ★ 주문서 처럼 여러 Page 에 걸쳐 같은 값을 가지고 작업할때에 SessionAttributes ★
@SessionAttributes({"requestAgree", "requestLogin", "authCodeVerified", "socialChannel", "socialToken"})
public class MemberController {

    // 공통 기능 의존 주입
    private final Utils utils;
    
    // 회원 가입 검증 의존 주입
    private final JoinValidator joinValidator;

    // 회원 가입 처리 의존 주입
    private final MemberUpdateService updateService;

    private final MemberDeleteService deleteService;

    private final MemberInfoService infoService;

    // 소셜 로그인 설정 조회용
    private final CodeValueService codeValueService;

    private final KakaoLoginService kakaoLoginService;

    @ModelAttribute("requestAgree")
    public RequestAgree requestAgree() {
        return new RequestAgree();
    }

    /* 회원 페이지 CSS */
    @ModelAttribute("addCss")
    public List<String> addCss() {

        return List.of("member/style");
    }

    @ModelAttribute("requestLogin")
    public RequestLogin requestLogin() {

        return new RequestLogin();
    }

    // 이메일 인증 여부
    @ModelAttribute("authCodeVerified")
    public boolean authCodeVerified() {

        return false;
    }

    @ModelAttribute("socialChannel")
    public SocialChannel socialChannel() {

        // 기본값 null, KakaoCallback 에서 set 설정 예정
        return SocialChannel.NONE;
    }

    @ModelAttribute("socialToken")
    public String socialToken() {

        // 기본값 null, KakaoCallback 에서 set 설정 예정
        return null;
    }

    @GetMapping("/login")
    public String login(@ModelAttribute RequestLogin form, Errors errors, Model model, HttpSession session) {

        // 로그인 페이지 공통 처리
        commonProcess("login", model);

        // 세션이 아닌 모델로 처리
        model.addAttribute("socialChannel", SocialChannel.NONE);
        model.addAttribute("socialToken", null);

        form.setKakaoLoginUrl(kakaoLoginService.getLoginUrl(form.getRedirectUrl()));

        // 로그인 검증 실패해서 에러코드가 있을 경우
        if (form.getErrorCodes() != null) {

            // NotBlank_email, NotBlank_password 로 만든 Errorcodes 를 Field & ErrorCode 로 쪼개서
            // validations.properties 와 같도록
            form.getErrorCodes().stream().map(s -> s.split("_"))
                    .forEach(s -> {
                        // s[1]이 있을 경우 NotBlank.email, NotBlank.password
                        if (s.length > 1) {
                            errors.rejectValue(s[1], s[0]);
                            // s[1]이 없을 경우 글로벌 오류
                            // Failure.validate.login
                        } else {
                            errors.reject(s[0]);
                        }
                    });
        }

        // uilts.tpl = PC / Mobile 분리
        return utils.tpl("member/login");
    }

    /*
    Spring Security로 대체


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
         /

        String redirectUrl = form.getRedirectUrl();

        // Stringutils = ThymeLeaf말고 반드시 Springframework
        redirectUrl = StringUtils.hasText(redirectUrl) ? redirectUrl : "/";

        // 이전 page OR main page 이동
        return "redirect:" + redirectUrl;
    }
    */

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
     *
     * - 필수 약관 동의 여부 검증
     *
     * @return
     */
    @PostMapping("/join")
    public String join(RequestAgree agree, Errors errors, @ModelAttribute RequestJoin form, Model model, @SessionAttribute(name = "socialChannel", required = false) SocialChannel socialChannel, @SessionAttribute(name = "socialToken", required = false) String socialToken) {
        
        // 회원 가입 공통 처리
        commonProcess("join", model);

        form.setSocialChannel(socialChannel);
        form.setSocialToken(socialToken);
        
        // 회원가입 양식 첫 유입에서는 Session 단위 저장인 이메일 인증 상태 false 로 초기화
        model.addAttribute("authCodeVerified", false);

        joinValidator.validate(agree, errors);

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
    public String joinPs(@SessionAttribute("requestAgree") RequestAgree agree, @Valid RequestJoin form, Errors errors, SessionStatus status, Model model, HttpSession session) {

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

        // Model 을 통해 수정(추가 & 변경)되는 것 방지
        // Session Set & Add 못하도록 완료 처리
        status.setComplete();

        // 회원가입 처리 완료 후 -> Login page 이동
        // return "redirect:/member/login";

        // 인증 관련 세션 정보 삭제
        session.removeAttribute("socialChannel");
        session.removeAttribute("socialToken");
        session.removeAttribute("authCodeVerified");

        // 회원 가입 처리 완료 후 member/registerend 이동
        return utils.tpl("member/registerend");
    }

    /**
     * Member 삭제
     * @param seq
     * @return
     */
    @ResponseBody
    @DeleteMapping ("/delete/{seq}")
    public JSONData delete (@PathVariable("seq") Long seq) {

        Member member = deleteService.delete(seq);

        return new JSONData(member);
    }

    /**
     * 회원 정보 갱신
     *
     */
    @ResponseBody
    @GetMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public void refresh(Principal principal, HttpSession session) {

        MemberInfo memberInfo = (MemberInfo) infoService.loadUserByUsername(principal.getName());

        session.setAttribute("member", memberInfo.getMember());
    }

    /**
     * 회원 소개 (공개용, DB 조회)
     *
     */
    @GetMapping("/about/{nickName}")
    public String about (@PathVariable("nickName") String nickName, Model model) {

        commonProcess("about", model);

        MemberInfo data = (MemberInfo) infoService.loadUserByNickName(nickName);

        Member member = data.getMember();

        model.addAttribute("member", member);
        model.addAttribute("seq", data.getMember().getSeq());
        // model.addAttribute("useFollowButton", true);

        return utils.tpl("member/about");
    }

    /**
     *
     * Controller 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "login";

        // Page 제목
        String pageTitle = null;;

        // 공통 JavaScript
        List<String> addCommonScript = new ArrayList<>();

        // Front 쪽에 추가할 JavaScript
        List<String> addScript = new ArrayList<>();

        List<String> addCss = new ArrayList<>();

        // 소셜 로그인 설정
        SocialConfig socialConfig = Objects.requireNonNullElseGet(codeValueService.get("socialConfig", SocialConfig.class), SocialConfig::new);

        // 로그인 공통 처리
        if (mode.equals("login")) {

            pageTitle = utils.getMessage("로그인");

        } else if (mode.equals("join")) {
            // 회원 가입 공통 처리
            pageTitle = utils.getMessage("회원가입");

            // common_address.js
            addCommonScript.add("address");

            // emailAuth.js 의 callback 처리는 join.js 에서 하게 됨
            addCommonScript.add("emailAuth");

            // front_member_join.js
            addScript.add("member/join");

        } else if (mode.equals("agree")) {

            pageTitle = utils.getMessage("약관동의");

            //약관 동의(agree) page 에 최초 접근시 약관 선택 초기화 (Session 비움)
            model.addAttribute("requestAgree", requestAgree());

        } else if (mode.equals("about")) {

            pageTitle = utils.getMessage("About_Me");

            addCommonScript.add("follow");

             addCss.add("mypage/about");

             addCss.add("pokemon/item");
        }

        // Page 제목
        model.addAttribute("pageTitle", pageTitle);

        // 공통 Script
        model.addAttribute("addCommonScript", addCommonScript);

        // Front 쪽에 추가할 Script
        model.addAttribute("addScript", addScript);

        model.addAttribute("socialConfig", socialConfig);
    }
}