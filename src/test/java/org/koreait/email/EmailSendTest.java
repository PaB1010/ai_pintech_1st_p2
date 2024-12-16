package org.koreait.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.koreait.email.controllers.RequestEmail;
import org.koreait.email.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Email 테스트
 *
 *
 */
@SpringBootTest
@ActiveProfiles({"default", "test", "email"})
public class EmailSendTest {

    // Spring 기본 기능
    @Autowired
    private JavaMailSender javaMailSender;

    // Spring 기본 기능
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private EmailService service;

    @Test
    void test1() throws Exception {

        /**
         * to : 받는 이메일
         * cc : 참조
         * bcc : 숨은 참조
         *
         */

        MimeMessage message = javaMailSender.createMimeMessage();

        // (MimeMessage 객체, 파일 첨부(멀티파트) 여부, 인코딩 언어)
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        // 수신인, 배열 오버로드시 여러명 수신 가능
        helper.setTo("ksw2662@gmail.com");

        // 제목
        helper.setSubject("테스트 이메일 제목...");

        // 내용
        helper.setText("테스트 이메일 내용...");
        
        // 가변 매개변수(...) 으로 여러개 발송 가능
        javaMailSender.send(message);
    }

    @Test
    void test2() {

        // thymeleaf.Context
        Context context = new Context();

        // EL 속성으로 Template 에서 바로 접근 가능(Model 유사)
        context.setVariable("subject", "테스트 제목...");

        String text = templateEngine.process("email/auth", context);

        System.out.println(text);
        /*
        <!DOCTYPE html>
        <html>
            <body>
                <h1>테스트 제목...</h1>
            </body>
        </html>
         */
    }

    @Test
    void test3() {

        RequestEmail form = new RequestEmail();

        form.setTo(List.of("ksw2662@gmail.com", "mudodd@naver.com"));
        form.setCc(List.of("ksw2662@gmail.com"));
        form.setBcc(List.of("ksw2662@gmail.com"));
        form.setSubject("테스트 이메일 제목...");
        form.setContent("<h1>테스트 이메일 내용...</h1>");

        Map<String, Object> tplData = new HashMap<>();

        tplData.put("key1", "값1");
        tplData.put("key2", "값2");

        boolean result = service.sendEmail(form, "auth", tplData);

        System.out.println(result);
    }
}