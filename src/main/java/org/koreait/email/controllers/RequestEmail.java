package org.koreait.email.controllers;

import lombok.Data;

import java.util.List;

/**
 * Email Data Class
 *
 * 커맨드 객체
 *
 */
@Data
public class RequestEmail {

    // 메일 수신 이메일, 여러개 가능하도록 List
    private List<String> to;

    // 참조 이메일, 여러개 가능하도록 List
    private List<String> cc;

    // 숨은 참조
    private List<String> bcc;

    // 메일 제목
    private String subject;

    // 메일 내용
    private String content;

}