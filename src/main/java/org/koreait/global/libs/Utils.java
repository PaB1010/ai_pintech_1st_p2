package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 메세지 코드를 배열로 받았을때 List로 변환해 반환해주는 기능
     * 
     * @param codes
     * @return
     */
    public List<String> getMessages(String[] codes) {

            return Arrays.stream(codes).map(c -> {

                try {
                    return getMessage(c);

                } catch (Exception e) {
                    return "";
                }
                // 비어있지 않은 문자열, 즉 코드만 걸러서 가져옴
            }).filter(s -> !s.isBlank()).toList();
    }

    /**
     * REST 커맨드 객체 검증 실패시에 Error Code를 가지고 Message를 추출하는 기능
     * @param errors
     * @return
     */
    public Map<String, List<String>> getErrorMessages(Errors errors) {

        // 형변환해도 싱글톤 객체
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;

        // 임시로 Message가 없으면 Key값(Message Code) 그대로 출력하는 기능을 false
        ms.setUseCodeAsDefaultMessage(false);

        try {
            // 필드별 Error Code - getFieldErrors()
            // FieldError = 커맨드 객체 검증 실패 & rejectValue(..)
            // Collectors.toMap = (Key = 필드명, Value = 메세지)
            Map<String, List<String>> messages = errors.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, f -> getMessages(f.getCodes()), (v1, v2) -> v2));
            // v1 = 처음 값, v2 = 마지막에 들어온 값
            // -> 중복될 경우 마지막 값으로 대체되도록 처리 (put과 유사)

            // 글로벌 Error Code - getGlobalErrors()
            // GlobalError = reject(..)
            List<String> gMessages = errors.getGlobalErrors()
                    .stream()
                    // flatMap = 중첩된 stream() 펼쳐서 1차원 배열로 변환
                    .flatMap(o -> getMessages(o.getCodes()).stream())
                    .toList();

            // Global ErrorCode Field = "global" 으로 임의 고정
            if (!gMessages.isEmpty()) {

                messages.put("global", gMessages);
            }

            return messages;

        } finally {

            // 임시로 Message가 없으면 Key값(Message Code) 그대로 출력하는 기능을 false
            // 했던 것을 다시 원래대로 true 복구
            // 싱글톤이라 복구 안하면 그대로 false로 남아서 사용하는 모든 곳에 영향
            ms.setUseCodeAsDefaultMessage(true);
        }
    }
}