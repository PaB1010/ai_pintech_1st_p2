package org.koreait.mypage.validators;

import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.libs.MemberUtil;
import org.koreait.mypage.controllers.RequestProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 회원 정보 수정 검증
 * 
 * 비밀번호는 수정 목적으로 값을 입력 받았을 때에만 검증
 *
 */
@Lazy
@Component
public class ProfileValidator implements Validator, PasswordValidator {

    @Autowired
    private MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.isAssignableFrom(RequestProfile.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestProfile form = (RequestProfile)target;

        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
        String nickName = form.getNickName();
        String email = form.getEmail();
        String mode = form.getMode();

        // 관리자 접근 모드인데 이메일이 없을 경우
        if (StringUtils.hasText(mode) && mode.equals("admin") && !StringUtils.hasText(email)) {

            errors.rejectValue("email", "NotBlack");
        }

        if (nickName.contains(" ")) {

            errors.rejectValue("nickName", "Whitespace");
        }

        if (!StringUtils.hasText(password)) {

            return;
        }

        if (password.length() < 8) {

            errors.rejectValue("password", "Size");
        }

        if (!StringUtils.hasText(confirmPassword)) {

            errors.rejectValue("confirmPassword", "NotBlank");

            return;
        }

        if (nickName.contains(" ")) {

            errors.rejectValue("nickName", "Whitespace");
        }

        // 비밀번호 복잡성 S

        if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharscehk(password)) {
            errors.rejectValue("password", "Complexity");
        }

        // 비밀번호 복잡성 E

        // 비밀번호, 비밀번호 확인 일치 여부 S

        if (!password.equals(confirmPassword)) {

            errors.rejectValue("confirmPassword", "Mismatch");
        }

        // 비밀번호, 비밀번호 확인 일치 여부 E
    }
}