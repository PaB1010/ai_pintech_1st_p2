package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;

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
}