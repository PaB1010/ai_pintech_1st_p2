package org.koreait.global.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * 모든 사용자 정의 예외 상위 Class
 *
 */
@Getter @Setter
public class CommonException extends RuntimeException{

    // HttpStatus = Enum Class
    private HttpStatus status;

    // 에러 코드가 맞나 체크
    private boolean errorCode;


    public CommonException(String message, HttpStatus status) {

        super(message);
        
        // status = null일 경우 HttpStatus.INTERNAL_SERVER_ERROR로 대체
        this.status = Objects.requireNonNullElse(status, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}