package org.koreait.email.exceptions;

import org.koreait.global.exceptions.BadRequestException;

/**
 * 이메일 인증 코드 불일치 예외
 *
 */
public class AuthCodeMismatchException extends BadRequestException {

    public AuthCodeMismatchException() {

        super("Mismatch.authCode");
        setErrorCode(true);
    }
}