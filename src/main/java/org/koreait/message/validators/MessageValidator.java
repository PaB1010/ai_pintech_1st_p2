package org.koreait.message.validators;

import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.controllers.RequestMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * RequestMessage 커맨드 객체 검증
 *
 */
@Lazy
@Component
@RequiredArgsConstructor
public class MessageValidator implements Validator {

    private final MemberUtil memberUtil;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.isAssignableFrom(RequestMessage.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RequestMessage form = (RequestMessage) target;

        String email = form.getEmail();

        boolean notice = form.isNotice();

        // 관리자가 아니고 && 공지 쪽지일 경우
        if (!memberUtil.isAdmin() && notice) {

            notice = false;

            form.setNotice(notice);
//          form.setNotice(false);
        }

        // 관리자가 아니고 && 공지 쪽지 아니고 && 이메일이 없을 경우
        if (!memberUtil.isAdmin() && !notice && !StringUtils.hasText(email)) {

            errors.rejectValue("email", "NotBlank");
        }
    }
}
