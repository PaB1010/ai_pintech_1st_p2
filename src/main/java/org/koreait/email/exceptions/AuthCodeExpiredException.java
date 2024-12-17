package org.koreait.email.exceptions;

import org.koreait.global.exceptions.BadRequestException;

/**
 * 이메일 인증 코드 만료 예외
 *
 */
public class AuthCodeExpiredException extends BadRequestException {

    public AuthCodeExpiredException() {

        super("Expired.authCode");
        setErrorCode(true);
    }
}