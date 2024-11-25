package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

/**
 * 수동 등록 객체 관리할 곳
 *
 */
@Configuration
public class BeansConfig {

    // @Lazy = 싱글톤
    @Lazy
    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }
}