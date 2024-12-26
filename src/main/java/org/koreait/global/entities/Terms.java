package org.koreait.global.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 약관 제목 & 약관 내용
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 기본 생성자 public 접근 가능하도록 Annotation 두개 추가해 편법으로 사용
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
