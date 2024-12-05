package org.koreait.global.advices;

import org.koreait.global.exceptions.CommonException;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * @RestControllerAdvice
 *
 * RestController 공통 처리 부분
 * 범위 @RestController
 *
 */
@RestControllerAdvice(annotations = RestController.class)
public class CommonRestControllerAdvice {

    // Error 도 항상 동일한 형식(JSONData 형식)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JSONData> errorHandler(Exception e) {

        // default Error Code = 500
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // CommonException에서 message를 String, Map으로 했으니 Object
        Object message = e.getMessage();

        // 정의한 예외일 경우 응답코드 get
        if (e instanceof CommonException commonException) {

            status = commonException.getStatus();

            Map<String, List<String>> errorMessages = commonException.getErrorMessages();

            if (errorMessages != null) {

                message = errorMessages;
            }
        }

        JSONData data = new JSONData();

        data.setSuccess(false);
        data.setStatus(status);
        data.setMessage(message);

        e.printStackTrace();

        return ResponseEntity.status(status).body(data);
    }
}