package org.koreait.member.validators;

import org.koreait.member.controllers.RequestAgree;
import org.koreait.member.controllers.RequestJoin;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 회원 가입 검증
 *
 */
// @Lazy = 지연 로딩 - 최초로 해당 Bean을 사용할 때 생성
@Lazy
@Component
public class JoinValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // 약관 동의 || 회원 가입 검증으로 분리
        return clazz.isAssignableFrom(RequestAgree.class) || clazz.isAssignableFrom(RequestJoin.class);
    }

    /**
     * 약관 동의인지 회원 가입인지 구분
     *
     * @param target the object that is to be validated
     * @param errors contextual state about the validation process
     */
    @Override
    public void validate(Object target, Errors errors) {

        if (target instanceof RequestAgree requestAgree) {

            validateAgree(new RequestAgree(), errors);
        } else {
            validateJoin((RequestJoin) target, errors);
        }
    }

    /**
     * 약관 동의 검증
     *
     * @param form
     * @param errors
     */
    private void validateAgree(RequestAgree form, Errors errors) {

    }

    /**
     * 회원 가입 검증
     *
     * @param form
     * @param errors
     */
    private void validateJoin(RequestJoin form, Errors errors) {

        /**
         * 1. 이메일 중복 여부 체크 - DB 연동후
         * 1-1. 이메일 인증 (예정)
         * 2. 비밀번호 복잡성 - 영어 대소문자 각각 1개 이상 & 숫자 1개 이상 & 특수 문자 포함 필수
         * 3. 비밀번호, 비밀번호 확인 일치 여부
         * 4. 생년월일을 입력 받으면 14세 이상만 가입 가능하게 통제
         * 2~4번 Validator에서 구현
         */

    }
}