package org.koreait.global.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * 접근 권한이 없는 페이지에 접근한 경우
 * 응답 코드 401 고정 (UnAuthorized)
 *
 */
public class UnAuthorizedException extends CommonException {

    public UnAuthorizedException() {

        this("UnAuthorized");
        setErrorCode(true);
    }

    public UnAuthorizedException(String message) {

        super(message, HttpStatus.UNAUTHORIZED);
    }
}