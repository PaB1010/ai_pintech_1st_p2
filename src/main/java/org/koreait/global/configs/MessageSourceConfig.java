// Message Source 설정

package org.koreait.global.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {

        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();

        ms.addBasenames("messages.commons","messages.validations","messages.errors");

        ms.setDefaultEncoding("UTF-8");

        // Message가 없으면 Key값(Message Code) 그대로 출력
        ms.setUseCodeAsDefaultMessage(true);

        return ms;
    }
}