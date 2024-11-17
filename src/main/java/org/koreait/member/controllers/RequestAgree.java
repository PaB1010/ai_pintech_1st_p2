package org.koreait.member.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RequestAgree {

    @NotNull
    // 필수 약관 동의 여부
    // 필수는 반드시 모두 선택 - 개수 체크만으로도 충분
    private boolean[] requiredTerms;

    // 선택 약관 동의 여부 - 문구 입력
    // 선택 약관은 어떤 약관을 체크했는지 구분할 수 있어야 함
    private List<String> optionalTerms;
}
