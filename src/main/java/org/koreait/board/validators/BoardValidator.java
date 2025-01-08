package org.koreait.board.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.board.controllers.RequestBoard;
import org.koreait.global.validators.PasswordValidator;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Lazy
@Component
@RequiredArgsConstructor
public class BoardValidator implements Validator, PasswordValidator {

    private final MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestBoard form = (RequestBoard) target;

        // 비회원 비밀번호 검증
        if (!memberUtil.isLogin()) {

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "guestPw", "NotBlank");
            
            // 대소문자 구분 없는 알파벳 1자 이상, 숫자 1자 이상 포함
            String guestPw = form.getGuestPw();
            
            if (StringUtils.hasText(guestPw) && (!alphaCheck(guestPw, true) || !numberCheck(guestPw))) {
                
                errors.rejectValue("guestPw", "Complexity");
            }
        }

        // 게시글 수정일 경우 seq 필수 검증
        String mode = form.getMode();

        Long seq = form.getSeq();

        if (mode != null && mode.equals("edit") && (seq == null || seq < 1L)) {

            errors.rejectValue("seq", "NotNull");
        }
    }
}