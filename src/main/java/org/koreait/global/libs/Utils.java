package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;

    private final MessageSource messageSource;

    // Browser 정보 Mobile 여부 확인
    public boolean isMobile() {

        // 요청 header -> User-Agent (Browser 정보)
        String ua = request.getHeader("User-Agent");
        
        // 해당 Pattern이 포함되면 Mobile로 판명
        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*";

        return ua.matches(pattern);
    }
    
    /*
        mobile / front template 분리 함수
     */
    public String tpl(String path) {
        
        String prefix = isMobile() ? "mobile" : "front";

        return String.format("%s/%s", prefix, path);
    }

    /**
     * 메세지 코드로 조회된 문구
     *
     * @param code
     * @return
     */
    public String getMessage(String code) {

        // 사용자 요청 header(Accept-Language)
        Locale lo = request.getLocale();

        return messageSource.getMessage(code, null, lo);
    }
}