package org.koreait.admin.basic.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.basic.services.TermsInfoService;
import org.koreait.admin.basic.services.TermsUpdateService;
import org.koreait.admin.global.menu.SubMenus;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.contants.Device;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.entities.Terms;
import org.koreait.global.libs.Utils;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.social.entities.SocialConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 기본(Basic) 관리자 설정 Controller
 *
 * Utils tpl 은 front/mobile 전용으로 사용하지 않음
 * 직접 Template 경로 반환
 *
 */
@Controller
@ApplyErrorPage
@RequestMapping("/admin/basic")
@RequiredArgsConstructor
public class BasicController implements SubMenus {

    private final CodeValueService codeValueService;

    private final TermsUpdateService termsUpdateService;

    private final TermsInfoService termsInfoService;

    private final HttpServletRequest request;

    private final Utils utils;

    // @Override
    @ModelAttribute("menuCode")
    public String menuCode() {

        return "basic";
    }

    /**
     * 사이트 기본 정보 설정
     * 
     * @param model
     * @return
     */
    @GetMapping({"", "/siteConfig"})
    public String siteConfig(Model model) {

        commonProcess("siteConfig", model);

        // 최초에 null 일 경우 새로 생성
        SiteConfig form = Objects.requireNonNullElseGet(codeValueService.get("siteConfig", SiteConfig.class), SiteConfig::new);

        // default 값 ALL
        form.setDevice(Objects.requireNonNullElse(form.getDevice(), Device.ALL));

        model.addAttribute("siteConfig", form);

        return "admin/basic/siteConfig";
    }

    /**
     * 사이트 기본 정보 설정 처리
     * 
     * @param form
     * @param model
     * @return
     */
    @PatchMapping("/siteConfig")
    public String sitConfigPs(SiteConfig form, Model model) {

        commonProcess("sitConfig", model);

        codeValueService.save("siteConfig", form);

        utils.showSessionMessage("저장되었습니다.");

        return "admin/basic/siteConfig";
    }

    /**
     * 약관 관리 양식 & 목록
     *
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/terms")
    public String terms(@ModelAttribute Terms form, Model model) {
        
        commonProcess("terms", model);

        List<Terms> items = termsInfoService.getList();

        model.addAttribute("items", items);

        return "admin/basic/terms";
    }

    /**
     * 약관 등록 처리
     *
     * @param form
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/terms")
    public String termsPs(@Valid Terms form, Errors errors, Model model) {

        commonProcess("terms", model);

        if (errors.hasErrors()) {

            return "admin/basic/terms";
        }

        termsUpdateService.save(form);

        // Script 추가시 부모창 새로고침하는 속성 추가
        model.addAttribute("script", "parent.location.reload();");

        return "common/_execute_script";
    }

    /**
     * 새로 고침
     *
     * @param chks
     * @return
     */
    @RequestMapping(path="/terms", method={RequestMethod.PATCH, RequestMethod.DELETE})
    public String updateTerms(@RequestParam(name="chk", required = false) List<Integer> chks, Model model) {

        termsUpdateService.processList(chks);

        String message = request.getMethod().equalsIgnoreCase("DELETE") ? "삭제" : "수정";

        message += "하였습니다.";

        utils.showSessionMessage(message);

        model.addAttribute("script", "parent.location.reload();");

        return "common/_execute_script";
    }

    /**
     * 소셜 로그인 설정 양식 & 목록
     *
     * @param model
     * @return
     */
    @GetMapping("/social")
    public String social(Model model) {

        commonProcess("social", model);

        SocialConfig form = codeValueService.get("siteConfig", SocialConfig.class);

        form = Objects.requireNonNullElseGet(form, SocialConfig::new);

        model.addAttribute("socialConfig", form);

        return "admin/basic/social";
    }

    /**
     * 소셜 로그인 설정 처리
     *
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/social")
    public String socialPs(SocialConfig form, Model model) {

        commonProcess("social", model);

        codeValueService.save("socialConfig", form);

        utils.showSessionMessage("저장되었습니다.");

        return "admin/basic/social";
    }

    /**
     * 기본(basic) 설정 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "siteConfig";
        
        String pageTitle = null;
        
        if (mode.equals("siteConfig")) {

            pageTitle = "사이트 기본 정보 설정";

        } else if (mode.equals("terms")) {

            pageTitle = "약관 관리";
            
        } else if (mode.equals("social")) {
            
            pageTitle = "소셜 설정";
        }

        pageTitle += " - 기본설정";

        model.addAttribute("pageTitle", pageTitle);
        
        model.addAttribute("subMenuCode", mode);

    }
}