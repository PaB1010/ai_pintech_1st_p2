package org.koreait.mypage.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyErrorPage;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.member.MemberInfo;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.MemberInfoService;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.mypage.services.FollowService;
import org.koreait.mypage.validators.ProfileValidator;
import org.koreait.pokemon.controllers.PokemonSearch;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.services.PokemonInfoService;
import org.koreait.wishlist.constants.WishType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/mypage")
@SessionAttributes("profile")
@RequiredArgsConstructor
@ApplyErrorPage
public class MypageController {

    private final Utils utils;

    private final MemberUtil memberUtil;

    private final ModelMapper modelMapper;

    private final MemberUpdateService updateService;

    private final ProfileValidator profileValidator;

    private final MemberInfoService memberInfoService;

    private final PokemonInfoService pokemonInfoService;

    private final FollowService followService;

    // profile 이라는 속성명을 가지고 template 에서 회원 조회를 바로
    // ★ About 에서 이거 가져다 쓰기 ★
    @ModelAttribute("profile")
    public Member getMember() {

        return memberUtil.getMember();
    }

    @GetMapping
    public String index(Model model) {

        commonProcess("main", model);

        return utils.tpl("mypage/index");
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        commonProcess("profile", model);

        Member member = memberUtil.getMember();
        // form.setName(member.getName());
        // form.setName(member.getNickName());

        RequestProfile form = modelMapper.map(member, RequestProfile.class);

        String optionalTerms = member.getOptionalTerms();

        // ★ 커맨드 객체 형태로 변환 ★
        if (StringUtils.hasText(optionalTerms)) {

            form.setOptionalTerms(Arrays.stream(optionalTerms.split("\\|\\|")).toList());
        }

        model.addAttribute("requestProfile", form);

        return utils.tpl("mypage/profile");
    }

    @PatchMapping("/profile")
    public String updateProfile(@Valid RequestProfile form, Errors errors, Model model) {

        commonProcess("profile", model);

        // ★ 추가 검증 ★
        profileValidator.validate(form, errors);

        if (errors.hasErrors()) {

            return utils.tpl("mypage/profile");
        }

        updateService.process(form);

        // 프로필 속성 변경
        model.addAttribute("profile", memberUtil.getMember());

        // 회원 정보 수정 완료 후 Mypage Main 이동
        return "redirect:/mypage";
    }

    /**
     * 회원 소개 (초기 테스트용)
     */
    @GetMapping("/about")
    public String about(Model model) {

        commonProcess("about", model);

        return utils.tpl("mypage/about");
    }


    /**
     * 찜하기 목록
     *
     * @param mode   : POKEMON (포켓몬 찜하기 목록), BOARD (게시글 찜하기 목록)
     * @param search
     * @param model
     * @return
     */
    @GetMapping({"/wishlist", "/wishlist/{mode}"})
    public String wishlist(@PathVariable(name = "mode", required = false) WishType mode, CommonSearch search, Model model) {

        commonProcess("wishlist", model);

        mode = Objects.requireNonNullElse(mode, WishType.POKEMON);

        if (mode == WishType.BOARD) {
            // 게시글 찜하기 목록

        } else if (mode == WishType.POKEMON) {
            // 포켓몬 찜하기 목록

            PokemonSearch pSearch = modelMapper.map(search, PokemonSearch.class);

            ListData<Pokemon> data = pokemonInfoService.getMyPokemons(pSearch);
            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());
        }

        return utils.tpl("mypage/wishlist/main");
    }

    /*
    @GetMapping({"/wishlist", "/wishlist/{mode}"})
    public String wishlist (@PathVariable(name="mode", required = false) String mode, @ModelAttribute PokemonSearch search, Model model) {

        commonProcess("wishlist", model);
        mode = StringUtils.hasText(mode) ? mode : "pokemon";

        if (mode.equals("pokemon")) {
            ListData<Pokemon> data = pokemonInfoService.getMyPokemons(search);
            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());
        } else if (mode.equals("board")) {

        }

        model.addAttribute("mode", mode);

        return utils.tpl("mypage/wishlist/main");
    }
     */

    /**
     * 회원 정보 갱신
     */
    @ResponseBody
    @GetMapping("/refresh")
    public void refresh(Principal principal, Model model, HttpSession session) {

        MemberInfo memberInfo = (MemberInfo) memberInfoService.loadUserByUsername(principal.getName());

        session.setAttribute("member", memberInfo.getMember());

        // 프로필 속성 변경
        model.addAttribute("profile", memberInfo.getMember());
    }

    /**
     * Follow 목록
     *
     * @param mode
     * @param paging
     * @param model
     * @return
     */
    @GetMapping("/follow")
    public String followList(CommonSearch paging, Model model) {

        commonProcess("follow", model);

        List<Member> followers = followService.getList("follower", paging).getItems();
        List<Member> followings = followService.getList("following", paging).getItems();

        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);

        return utils.tpl("mypage/follow/follow");
    }

//    @GetMapping("/follow")
//    public String followList(@RequestParam(name="mode", defaultValue = "follower") String mode, CommonSearch paging, Model model) {
//
//        commonProcess("follow", model);
//
//        ListData<Member> data = followService.getList(mode, paging);
//
//        model.addAttribute("items", data.getItems());
//        model.addAttribute("pagination", data.getPagination());
//        model.addAttribute("mode", mode);
//
//        return utils.tpl("mypage/follow/follow");
//    }

    /**
     * Controller 공통 처리 영역
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {

        // mode 가 없을 경우 main 으로 기본 값
        mode = StringUtils.hasText(mode) ? mode : "main";

        String pageTitle = utils.getMessage("마이페이지");

        // 공통 script
        List<String> addCommonScript = new ArrayList<>();

        // Front script
        List<String> addScript = new ArrayList<>();

        List<String> addCss = new ArrayList<>();

        addCss.add("mypage/style");

        // 회원 정보 수정
        if (mode.equals("profile")) {

            addCommonScript.add("fileManager");
            addCommonScript.add("address");
            addScript.add("mypage/profile");
            pageTitle = utils.getMessage("회원정보_수정");

        } else if (mode.equals("about")) {

            pageTitle = utils.getMessage("About_Me");
            addCss.add("mypage/about");
            addCss.add("pokemon/item");

        } else if (mode.equals("wishlist")) {

            addCommonScript.add("wish");
            pageTitle = utils.getMessage("My_Wish");
            // 추후 포켓몬 list 와 Css 공유할 경우 add
        } else if (mode.equals("follow")) {

            // addCommonScript.add("follow");
            pageTitle = utils.getMessage("My_Follow");

        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
    }
}