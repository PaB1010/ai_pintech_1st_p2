package org.koreait.admin.basic.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.global.menu.MenuDetail;
import org.koreait.admin.global.menu.Menus;
import org.koreait.global.annotations.ApplyErrorPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    @ModelAttribute("menuCode")
    public String menuCode() {

        return "basic";
    }

    @ModelAttribute("submenus")
    public List<MenuDetail> submenus() {

        return Menus.getMenus(menuCode());
    }

    @GetMapping({"", "/siteConfig"})
    public String siteConfig(Model model) {

        commonProcess("siteConfig", model);

        return "admin/basic/siteConfig";
    }

    /**
     * 기본(basic) 설정 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        model.addAttribute("subMenuCode", mode);

    }
}