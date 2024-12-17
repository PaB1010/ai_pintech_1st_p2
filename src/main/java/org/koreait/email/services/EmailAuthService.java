package org.koreait.email.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.email.controllers.RequestEmail;
import org.koreait.email.exceptions.AuthCodeExpiredException;
import org.koreait.email.exceptions.AuthCodeMismatchException;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Email 인증 서비스
 *
 */
@Service
@RequiredArgsConstructor
@Profile("email")
public class EmailAuthService {

    // getMessage()
    private final Utils utils;

    private final EmailService emailService;

    // Session 주입용
    private final HttpSession session;

    /**
     * 인증 코드 발송
     *
     * 1. 5자리 랜덤 정수
     * 2. 만료시간 (현재 시간 기준)3분 기록
     * 3. 사용자의 입력을 검증하기 위해
     *    Session 에 인증 코드 & 만료 시간 기록
     *    개개인의 서비스를 위해 Session
     *    요청 간에 Data 가 유지 되어야 함
     *
     * @param to : 수신 이메일 주소
     * @return
     */
    public boolean sendCode(String to) {

        Random random = new Random();

        String subject = utils.getMessage("Email.authCode.subject");

        // 5자리 내의 랜덤 정수 인증 코드
        int authCode = random.nextInt(99999);

        // 현재 시간 기준 3분 뒤로 만료 시간 기록
        LocalDateTime expired = LocalDateTime.now().plusMinutes(3L);

        // Session 에 인증 코드 & 만료 시간 기록
        session.setAttribute("authCode", authCode);
        session.setAttribute("expiredTime", expired);
        // Session 에 인증 실패 상태(인증 완료전이니 실패 상태) 기록
        session.setAttribute("authCodeVerified", false);

        Map<String, Object> tplData = new HashMap<>();

        tplData.put("authCode", authCode);

        RequestEmail form = new RequestEmail();

        form.setTo(List.of(to));
        form.setSubject(subject);

        return emailService.sendEmail(form, "auth", tplData);
    }

    /**
     * 인증 코드 검증
     *
     * code null 가능성이 있으니 Integer
     *
     * @param code : 사용자가 입력한 인증 코드
     */
    public void verify(Integer code) {

        if (code == null) {

            throw new BadRequestException(utils.getMessage("NotBlank.authCode"));
        }

        // 검증할 Session 값 get
        LocalDateTime expired = (LocalDateTime)session.getAttribute("expiredTime");
        int authCode = (int)session.getAttribute("authCode");

        // 만료된 인증 코드일 경우
        if (expired.isBefore(LocalDateTime.now())) {

            throw new AuthCodeExpiredException();
        }

        // 인증 코드 불일치일 경우
        if (!code.equals(authCode)) {

            throw new AuthCodeMismatchException();
        }

        // Session 에 인증 성공 상태 기록
        session.setAttribute("authCodeVerified", true);
    }
}