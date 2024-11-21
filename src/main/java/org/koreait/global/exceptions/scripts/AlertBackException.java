package org.koreait.global.exceptions.scripts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * 예외가 발생하면
 * alert('message');
 * target.history.back();
 *
 */
@Getter @Setter
public class AlertBackException extends AlertException {

    private String target;

    public AlertBackException(String message, HttpStatus status, String target) {

        super(message, status);

        // target이 있으면 target을 사용하고 없을경우 기본값으로 현재 창(self)에 이동
        target = StringUtils.hasText(target) ? target : "self";

        this.target = target;
    }

    /* 생성자 오버로드 */
    public AlertBackException(String message, HttpStatus status) {

        this(message, status, null);
    }

    /* 생성자 오버로드 */
    public AlertBackException(String message) {

        this(message, null);
    }
}
