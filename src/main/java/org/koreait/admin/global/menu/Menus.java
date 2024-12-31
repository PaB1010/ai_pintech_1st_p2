package org.koreait.admin.global.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean 아닌 정적으로 사용
 * 관리자 공통 메뉴
 *
 */
public class Menus {

    private static Map<String, List<MenuDetail>> submenus;

    // 처음에 바로 Load 되도록
    static {

        submenus = new HashMap<>();

        // 기본 설정
        submenus.put("basic", List.of(

            new MenuDetail("siteConfig", "사이트 기본정보", "/admin/basic/siteConfig"),

            // 약관은 많이 쓰니 회원쪽 한정이 아닌 공용으로
            new MenuDetail("terms", "약관 관리", "/admin/basic/terms")
        ));

        // 회원 관리
        submenus.put("member", List.of(

            new MenuDetail("list", "회원 목록", "/admin/member/list"),

            new MenuDetail("message", "쪽지 관리", "/admin/member/message")
        ));

        // 게시판 관리
        submenus.put("board", List.of(

           new MenuDetail("list", "게시판 목록", "/admin/board/list"),

           new MenuDetail("add", "게시판 등록", "/admin/board/add"),

           new MenuDetail("posts", "게시글 관리", "/admin/board/posts")
        ));

        // 상품 관리

        submenus.put("product", List.of(

           new MenuDetail("list", "상품 목록", "/admin/product/list"),

           new MenuDetail("add", "상품 등록", "/admin/product/add"),

           new MenuDetail("category", "분류 관리", "/admin/product/category"),

           new MenuDetail("delivery", "배송 정책 관리", "/admin/product/delivery")
        ));

        // 공지 (웹소켓 이용) 추가 예정
    }

    // menuCode 받아서 Menu 반환하는 편의 기능
    public static List<MenuDetail> getMenus(String menuCode) {

        return submenus.get(menuCode);
    }
}