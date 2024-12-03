package org.koreait.member.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.controllers.RequestLogin;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 로그인 실패시 상세 처리
 *
 */
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // requestLogin이 MemberController에서 @SessionAttributes({"requestAgree", "requestLogin"})라서 Session 가져옴
        HttpSession session = request.getSession();

        // 값이 없을 경우도 대비해 Objects.requireNonNullElse
        RequestLogin form = Objects.requireNonNullElse((RequestLogin)session.getAttribute("RequestLogin"), new RequestLogin());

        form.setErrorCodes(null);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // ID | PW를 입력하지 않은 경우
        // ID 조회 X, PW 일치 X -> 둘중 뭐가 X인지 모르게 애매한 메세지
        if (exception instanceof BadCredentialsException) {

            // form.getErrorCodes가 null 일경우 새로운 ArrayList 객체 생성
            List<String> errorCodes = Objects.requireNonNullElse(form.getErrorCodes(), new ArrayList<>());

            // email이 비어있을 경우
            if (!StringUtils.hasText(email)) {

                errorCodes.add("NotBlank_email");
            }

            // PW가 비어있을 경우
            if (!StringUtils.hasText(password)) {

                errorCodes.add("NotBlank_password");
            }

            // 둘다 아닐 경우 무조건 ID 혹은 PW 불일치
            if (errorCodes.isEmpty()) {
                errorCodes.add("Failure.validate.login");
            }

            // 원래 있던 객체라면 set 안해도 되지만 NonNullElse로 새로운 ArrayList 객체가 생성될 수도 있으므로 set
            form.setErrorCodes(errorCodes);
        }

        session.setAttribute("requestLogin", form);

        // 로그인 실패시 다시 로그인 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/member/login");
    }
}