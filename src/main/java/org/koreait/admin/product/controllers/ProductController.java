package org.koreait.admin.product.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.admin.global.menu.SubMenus;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 상품 관리
 *
 * 상품 생성 가능한 Controller 로
 * Product 도메인 작업시 가장 먼저 작업!
 */
@ApplyErrorPage
@Controller("adminProductController")
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController implements SubMenus {

    private final Utils utils;

    @Override
    @ModelAttribute("menuCode")
    public String menuCode() {
        return "product";
    }

    /**
     * 상품 목록
     * 
     * @param model
     * @return
     */
    @GetMapping({"", "/list"})
    public String list(Model model) {

        commonProcess("list", model);

        return "admin/product/list";
    }

    /**
     * 상품 등록 양식
     * 
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String add(Model model) {

        commonProcess("add", model);

        // 양식에서 추가할 것이 많아서 replace 사용 불가피
        return "admin/product/add";
    }
    
    // add 와 edit 양식은 내부에서는 공유하도록 처리 예정

    /**
     * 상품 정보 수정
     *
     * @param seq
     * @param model
     * @return
     */
    @GetMapping("/edit/{seq}")
    public String edit(@PathVariable("seq") Long seq, Model model) {

        commonProcess("edit", model);

        return "admin/product/edit";
    }

    /**
     * 상품 등록 & 수정 처리
     *
     * @return
     */
    @PostMapping("/save")
    public String save(Model model) {

        commonProcess("", model);

        return "redirect:/admin/product/list";
    }

    /**
     * 상품 분류 목록
     *
     * @param model
     * @return
     */
    @GetMapping("/category")
    public String categoryList(Model model) {

        commonProcess("category", model);

        return "admin/product/category/list";
    }

    /**
     * 분류 등록 & 수정
     *
     * @param model
     * @return
     */
    @GetMapping({"/category/add", "/category/edit/{cate}"})
    public String categoryUpdate(@PathVariable(name = "cate", required = false) String cate, Model model) {

        commonProcess("category", model);

        return "admin/product/category/add";
    }

    /**
     * 분류 등록 & 수정 처리
     * 
     * @param model
     * @return
     */
    @PostMapping("/category/save")
    public String categorySave(Model model) {

        commonProcess("category", model);

        return "redirect:/admin/product/category";
    }

    /**
     * 배송 정책 관리
     *
     * @param model
     * @return
     */
    @GetMapping("/delivery")
    public String delivery(Model model) {

        commonProcess("delivery", model);

        return "admin/product/delivery/list";
    }

    /**
     * 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        model.addAttribute("subMenuCode", mode);

//        if (mode.equals("list")) {
//
//        } else if (mode.equals("product_save")) {
//
//        } else if (mode.equals("category_list")) {
//
//        }
    }
}