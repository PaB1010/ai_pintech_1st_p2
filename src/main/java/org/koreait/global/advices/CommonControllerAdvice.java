package org.koreait.global.advices;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.exceptions.CommonException;
import org.koreait.global.exceptions.scripts.AlertBackException;
import org.koreait.global.exceptions.scripts.AlertException;
import org.koreait.global.exceptions.scripts.AlertRedirectException;
import org.koreait.global.libs.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 모든 @Controller 에 적용되는 공통 기능
 *
 */
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class CommonControllerAdvice {

    private final Utils utils;

    @ExceptionHandler(Exception.class)
    public ModelAndView errorHandler(Exception e, HttpServletRequest request) {

        Map<String, Object> data = new HashMap<>();

        // 기본 응답 코드 500
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // 기본 출력 Template
        String tpl = "error/error";

        String message = e.getMessage();

        // 자주 쓰이는 요청 데이터들 S
        data.put("method", request.getMethod());

        data.put("path", request.getContextPath() + request.getRequestURI());

        data.put("querystring", request.getQueryString());

        data.put("exception", e);

        // 자주 쓰이는 요청 데이터들 E

        if (e instanceof CommonException commonException) {

            status = commonException.getStatus();

            // ErrorCode로 message를 출력하는 것이라면 true
            message = commonException.isErrorCode() ? utils.getMessage(message) : message;

            StringBuffer sb = new StringBuffer(2048);

            if (e instanceof AlertException) {

                // Script 실행을 위한 HTML Template
                tpl = "common/_execute_script";

                sb.append(String.format("alert('%s');", message));
            }

            if (e instanceof AlertBackException backException) {

                String target = backException.getTarget();

                sb.append(String.format("%s.history.back();", target));
            }

            if (e instanceof AlertRedirectException redirectException) {

                String target = redirectException.getTarget();

                String url = redirectException.getUrl();

                // replace -> method=POST 일경우 Back 할때마다 Data가 계속 들어가는 문제 해결
                sb.append(String.format("%s.location.replace(%s);", target, url));
            }

            if(!sb.isEmpty()) {

                data.put("script", sb.toString());
            }
        }

        // addCss S

        List<String> addCss = (List<String>) data.get("addCss");

        if (addCss == null) {
            addCss = new ArrayList<>();
            data.put("addCss", addCss);
        }

        // error/style 추가
        addCss.add("error/style");

        // addCss E

        // 숫자 형태
        data.put("status", status.value());
        // 문자 형태
        data.put("_status", status);

        data.put("message", message);

        // data.put("addCss", List.of("error/style"));

        ModelAndView mv = new ModelAndView();

        mv.setStatus(status);
        mv.addAllObjects(data);
        mv.setViewName(tpl);

        System.out.println("addCss: " + addCss);

        return mv;
    }

}