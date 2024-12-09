package org.koreait.file.controllers;

import lombok.Data;

/**
 * 썸네일 커맨드 객체
 *
 */
@Data
public class RequestThumb {
    
    
    private Long seq;

    // 원격 IMG URL
    private String url;
    
    // seq & url 둘중 하나는 무조건 필수

    private int width;

    private int height;
}