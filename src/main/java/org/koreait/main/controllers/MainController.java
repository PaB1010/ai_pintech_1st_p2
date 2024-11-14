package org.koreait.main.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;

    // 상단쪽 클래스명 위의 @RequestMappin의 ("/") 값이 똑같으면 생략 가능
    @GetMapping
    public String index(Model model) {

        /*
        // static.front에 생성되도록, List & Array 모두 가능
        model.addAttribute("addCss", List.of("member/test1", "member/test2"));
        model.addAttribute("addScript", new String[] {"member/test1", "member/test2"});

        return "front/main/index";
         */

        return utils.tpl("main/index");
    }

}