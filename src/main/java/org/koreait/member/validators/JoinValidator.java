package org.koreait.member.validators;

import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.controllers.RequestAgree;
import org.koreait.member.controllers.RequestJoin;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

/**
 * 회원 가입 검증
 *
 */
// @Lazy = 지연 로딩 - 최초로 해당 Bean을 사용할 때 생성
@Lazy
@Component
public class JoinValidator implements Validator, PasswordValidator {

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

        // 커맨드 객체 검증 실패시에는 추가 검증은 진행 X
        // MemberController.joinPS(@Valid RequestJoin form)
        if (errors.hasErrors()) {

            return;
        }

        if (target instanceof RequestJoin requestJoin) {

            validateJoin(requestJoin, errors);
        } else {
            validateAgree((RequestAgree) target, errors);
        }
    }

    /**
     * 약관 동의 검증
     *
     * @param form
     * @param errors
     */
    private void validateAgree(RequestAgree form, Errors errors) {

        if (errors.hasErrors()) {

            errors.getAllErrors().stream().forEach(System.out::println);

            return;
        }

        if (!form.isRequiredTerms1()) {

            errors.rejectValue("requiredTerms1", "AssertTrue");
        }

        if (!form.isRequiredTerms2()) {

            errors.rejectValue("requiredTerms2", "AssertTrue");
        }

        if (!form.isRequiredTerms3()) {

            errors.rejectValue("requiredTerms3", "AssertTrue");
        }
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
         * 2. 비밀번호 복잡성 - 8 ~ 40글자 & 영어 대소문자 각각 1개 이상 & 숫자 1개 이상 & 특수 문자 포함 필수 (global_validators : interface - 비회원 이용등 다른 곳에서도 사용하기 때문에)
         * 3. 비밀번호 - 비밀번호 확인 일치 여부
         * 4. 생년월일을 입력 받으면 만 14세 이상만 가입 가능하게 통제
         * 2~4번 Validator에서 구현
         */

        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
        LocalDate birthDt = form.getBirthDt();

        // 2. 비밀번호 복잡성 S

        if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharscehk(password)) {
            System.out.println("------ 확인 ------");
            System.out.println(password);
            System.out.println(alphaCheck(password, false));
            System.out.println(numberCheck(password));
            System.out.println(specialCharscehk(password));
            errors.rejectValue("password", "Complexity");
            System.out.println("------ 확인 ------");
        }

        // 2. 비밀번호 복잡성 E

        // 3. 비밀번호, 비밀번호 확인 일치 여부 S

        if (!password.equals(confirmPassword)) {

            errors.rejectValue("confirmPassword", "Mismatch");
        }

        // 3. 비밀번호, 비밀번호 확인 일치 여부 E

        // 4. 생년월일을 입력 받으면 만 14세 이상만 가입 가능하게 통제 S

        Period period = Period.between(birthDt, LocalDate.now());

        int year = period.getYears();

        // 만 14세 미만인 경우
        if (year < 14) {

            errors.rejectValue("birthDt", "UnderAge");
        }

        // 4. 생년월일을 입력 받으면 만 14세 이상만 가입 가능하게 통제 E

    }
}