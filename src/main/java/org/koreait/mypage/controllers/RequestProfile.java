package org.koreait.mypage.controllers;

import lombok.Data;

/**
 * Mypage Data Class
 *
 */
@Data
public class RequestProfile {

    // 회원명
    private String name;

    private String nickName;

    private String password;

    private String confirmPassword;
}