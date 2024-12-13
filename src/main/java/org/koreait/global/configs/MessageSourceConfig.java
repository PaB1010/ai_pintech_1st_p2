package org.koreait.global.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Message Source 설정
 *
 */
@Configuration
public class MessageSourceConfig {

    @Bean
    // ★ 메세지 소스 메서드명은 고정 ★
    public MessageSource messageSource() {

        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();

        ms.addBasenames("messages.commons","messages.validations","messages.errors","messages.pokemon");

        ms.setDefaultEncoding("UTF-8");

        // Message가 없으면 Key값(Message Code) 그대로 출력
        ms.setUseCodeAsDefaultMessage(true);

        return ms;
    }
}