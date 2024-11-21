package org.koreait.member.controllers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.koreait.member.constants.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestJoin extends RequestAgree {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name; // 회원명

    @NotBlank
    @Size(min=8, max=40)
    private String password;

    @NotBlank
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
}