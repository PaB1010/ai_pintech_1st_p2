package org.koreait.member.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestLogin {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Login 완료 후 이동할 주소가 있으면 여기로 없으면 main 페이지로
    private String redirectUrl;
}