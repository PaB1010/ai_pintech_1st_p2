package org.koreait.global.exceptions.scripts;

import org.koreait.global.exceptions.CommonException;
import org.springframework.http.HttpStatus;

/**
 * 예외 발생하면 JavaScript alert('message'); 실행
 *
 * target.history.back()
 * 
 */
public class AlertException extends CommonException {

    public AlertException(String message) {

        this(message, null);
    }

    public AlertException(String message, HttpStatus status) {

        super(message, status);
    }
}