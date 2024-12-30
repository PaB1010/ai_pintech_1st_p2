package org.koreait.mypage.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.constants.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Mypage Data Class
 *
 */
@Data
public class RequestProfile {

    // 관리자쪽 접근 / 일반 회원쪽 접근 구분
    private String mode;

    private String email;

    // 회원명
    @NotBlank
    private String name;

    @NotBlank
    private String nickName;

    // @Size(min=8, max=40)
    private String password;

    private String confirmPassword;

    @NotNull
    private Gender gender;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String address;

    private String addressSub;

    // 추가 선택 약관
    private List<String> optionalTerms;

    private String bio;

    // 관리자만 수정 가능하게 할당
    private List<Authority> authorities;

    private FileInfo profileImage;
}