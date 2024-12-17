package org.koreait.email.exceptions;

import org.koreait.global.exceptions.BadRequestException;

/**
 * 이메일 인증 코드 발급 실패 예외
 *
 */
public class AuthCodeIssueException extends BadRequestException {

    public AuthCodeIssueException() {

        super("Fail.authCode.issue");
        setErrorCode(true);
    }
}