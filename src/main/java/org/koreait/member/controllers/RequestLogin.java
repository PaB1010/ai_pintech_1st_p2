package org.koreait.member.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestLogin implements Serializable {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Login 완료 후 이동할 주소가 있으면 여기로 없으면 main 페이지로
    private String redirectUrl;
    
    // Email & PW 검증 후 ErrorCode
    private List<String> errorCodes;
}