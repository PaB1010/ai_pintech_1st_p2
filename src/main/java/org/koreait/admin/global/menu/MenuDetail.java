package org.koreait.admin.global.menu;

/**
 * 메뉴 Data Class
 * -> 변경 사항 없을 곳일듯 하여 Record Class 활용
 *
 */
public record MenuDetail(

        // sub menu code
    String code,

        // sub menu name
    String name,

        // sub menu URL
    String url
) {}