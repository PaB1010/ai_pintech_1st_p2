package org.koreait.admin.basic.services;

import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.Terms;
import org.koreait.global.services.CodeValueService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 약관 관리 추가 & 수정 기능
 * JSON 형태로 기록
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class TermsUpdateService {

    private final CodeValueService service;

    /**
     * 약관 저장
     *
     * @param terms
     */
    public void save(Terms terms) {
        
        String code = String.format("term_%s", terms.getCode());

        service.save(code, terms);
    }
}