package org.koreait.admin.global.menu;

import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 
 * 
 */
public interface SubMenus {
    
    // 추상 메서드
    String menuCode();

    // default 메서드
    @ModelAttribute("submenus")
    default List<MenuDetail> submenus() {

        return Menus.getMenus(menuCode());
    }
}