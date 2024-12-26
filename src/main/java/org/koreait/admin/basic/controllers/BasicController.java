package org.koreait.admin.basic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.basic.services.TermsUpdateService;
import org.koreait.admin.global.menu.MenuDetail;
import org.koreait.admin.global.menu.Menus;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.entities.Terms;
import org.koreait.global.libs.Utils;
import org.koreait.global.services.CodeValueService;
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
public class BasicController {

    private final CodeValueService codeValueService;

    private final TermsUpdateService termsUpdateService;

    private final Utils utils;

    @ModelAttribute("menuCode")
    public String menuCode() {

        return "basic";
    }

    @ModelAttribute("submenus")
    public List<MenuDetail> submenus() {

        return Menus.getMenus(menuCode());
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

        return "admin/basic/terms";
    }

    /**
     * 약관 등록 처리
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

        // 임시
        return "common/_excute_script";
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
        }

        pageTitle += " - 기본설정";

        model.addAttribute("pageTitle", pageTitle);
        
        model.addAttribute("subMenuCode", mode);

    }
}