package org.koreait.pokemon.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Pokemon Controller
 *
 */
@Controller
@ApplyErrorPage
@RequestMapping("/pokemon")
@RequiredArgsConstructor
public class PokemonController {

    private final Utils utils;

    @GetMapping("/list")
    public String list(Model model) {

        commonProcess("list", model);

        return utils.tpl("pokemon/list");
    }

    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {

        commonProcess("view", model);

        return utils.tpl("pokemon/view");
    }

    // 메서드 오버로드 예정
    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "list";

        String pageTitle = utils.getMessage("포켓몬_도감");

        List<String> addCss = new ArrayList<>();

        // 포켓몬 도감 Page Common Style (목록 & 상세)
        addCss.add("pokemon/style");

        if (mode.equals("list")) {

            // 목록 Style
            addCss.add("pokemon/list");

        } else if (mode.equals("view")) {

            // 상세 style
            addCss.add("pokemon/view");
        }

        // view Title 추후 가공 예정
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);

    }
}