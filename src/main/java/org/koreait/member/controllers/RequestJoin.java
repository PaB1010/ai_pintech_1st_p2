package org.koreait.member.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.member.constants.Gender;
import org.koreait.member.social.constants.SocialChannel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Data
public class RequestJoin extends RequestAgree {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name; // 회원명

    @Size(min=8, max=40)
    private String password;

    private String confirmPassword;

    @NotBlank
    @Size
    private String nickName;

    @NotNull
    // @PastOrPresent // 현재 날짜 포함
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;

    @NotNull
    private Gender gender; // Enum class - 성별 (member.constants.Gender)

    @NotBlank
    private String zipCode; // 우편 번호

    @NotBlank
    private String address;

    private String addressSub;

    // 소셜 로그인시 hidden 값으로 실어 요청할 예정
    private SocialChannel socialChannel;
    private String socialToken;
    
    // socialChannel & socialToken 있을 경우
    // 소셜 로그인으로 가입하는 것인지 체크
    public boolean isSocial() {
        
        return socialChannel != null && StringUtils.hasText(socialToken);
    }
}