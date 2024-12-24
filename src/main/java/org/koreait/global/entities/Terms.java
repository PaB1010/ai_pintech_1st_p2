package org.koreait.global.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 약관 제목 & 약관 내용
 * 
 */
@Data
public class Terms {

    // 약관 코드 EX) terms_{c
    @NotBlank
    private String code;
    
    // 약관 제목
    @NotBlank
    private String subject;
    
    // 약관 내용
    @NotBlank
    private String content;
}
