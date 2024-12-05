package org.koreait.global.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

/**
 * 주로 검증 실패시 사용하는 예외
 * 응답 코드 400 고정 (Bad Request)
 */
public class BadRequestException extends CommonException{

    public BadRequestException() {

        this("BadRequest");
        setErrorCode(true);
    }

    public BadRequestException(String message) {

        super(message, HttpStatus.BAD_REQUEST);
    }

    // Rest 용 생성자 오버로드
    public BadRequestException(Map<String, List<String>> messages) {

        super(messages, HttpStatus.BAD_REQUEST);
    }
}