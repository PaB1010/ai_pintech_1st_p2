package org.koreait.email.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.koreait.email.controllers.RequestEmail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// @Lazy
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    /**
     *
     * 핵심 로직
     * Template 를 가져와 번역
     * 
     * @param form
     * @param tpl : Template Code = email/{tpl}.html
     * @param tplData : Template에 전달하는 Data (EL 속성으로 추가)
     * @return
     */
    public boolean sendEmail(RequestEmail form, String tpl, Map<String, Object> tplData) {
        try {
            // thymeleaf.Context
            Context context = new Context();

            // tplData 없을 경우 기본 생성 (메서드 참조)
            tplData = Objects.requireNonNullElseGet(tplData, HashMap::new);

            List<String> to = form.getTo();
            List<String>cc = form.getCc();
            List<String>bcc = form.getBcc();
            String subject = form.getSubject();
            String content = form.getContent();

            // template 에 출력할 Data 로 활용할 수 있게
            tplData.put("to", to);
            tplData.put("cc", cc);
            tplData.put("bcc", bcc);
            tplData.put("subject", subject);
            tplData.put("content", content);

            context.setVariables(tplData);

            String html = templateEngine.process("email/" + tpl, context);

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(form.getTo().toArray(String[]::new));

            // 참조(cc), 숨은 참조(bcc)는 있을 때에만 set

            if (cc != null && !cc.isEmpty()) {

                helper.setCc(cc.toArray(String[]::new));
            }

            if (bcc != null && !bcc.isEmpty()) {

                helper.setBcc(bcc.toArray(String[]::new));
            }

            // 번역된 Content set
            helper.setSubject(subject);
            helper.setText(html, true);

            // 반환값 void, 예외 발생 여부로 전송 됐나 안됐나 판단 가능
            javaMailSender.send(message);

            return true;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}