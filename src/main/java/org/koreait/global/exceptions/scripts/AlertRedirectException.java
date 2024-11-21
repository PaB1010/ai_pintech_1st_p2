package org.koreait.global.exceptions.scripts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * alret 후 왔던 경로로 되돌아가게
 *
 */
@Getter @Setter
public class AlertRedirectException extends AlertException {

    private String target;

    // 이동할 경로
    private String url;

    public AlertRedirectException(String message, String url, HttpStatus status, String target) {
        super(message, status);

        target = StringUtils.hasText(target) ? target : "self";

        this.url = url;
        this.target = target;

    }

    /* 생성자 오버로드 */
    public AlertRedirectException(String message, String url, HttpStatus status) {

        this(message, url, status, null);
    }

    /* 생성자 오버로드 */
    public AlertRedirectException(String message, String url) {

        this(message, url, null);
    }
}