package org.koreait.member.controllers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.koreait.member.constants.Gender;

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
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    @Size
    private String nickName;

    @NotNull
    private LocalDate brithDT;

    @NotNull
    private Gender gender; // Enum class - 성별 (member.constants.Gender)

    @NotBlank
    private String zipCode; // 우편 번호

    @NotBlank
    private String address;

    private String addressSub;
}