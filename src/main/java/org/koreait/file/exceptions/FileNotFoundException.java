package org.koreait.file.exceptions;

import org.koreait.global.exceptions.scripts.AlertBackException;
import org.springframework.http.HttpStatus;

/**
 * File 단일 조회 값 NULL 일경우
 * 후속 처리용 사용자 정의 예외
 * 
 * 후속 처리 = 이전 페이지로 이동 (AlertBackException)
 *
 * JAVA.io 쪽 예외 X
 *
 */
public class FileNotFoundException extends AlertBackException {

    public FileNotFoundException() {

        super("NotFound.file", HttpStatus.NOT_FOUND);

        setErrorCode(true);
    }
}