package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Internationalization : 국제화 설정
 *
 * 언어 코드 Query String 으로 설정할 예정
 * EX) ?language=ko
 *
 */
@Configuration
public class I18NConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localeChangeInterceptor());
                // .addPathPatterns("/**")
                // 사이트 전역에 적용하는 패턴일 경우 생략 가능 default 값이 "/**"
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {

        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();

        // Query String
        // ?language=en
        interceptor.setParamName("language");

        return interceptor;
    }

    @Bean
    public CookieLocaleResolver localeResolver() {

        CookieLocaleResolver resolver = new CookieLocaleResolver();

        resolver.setCookieName("language");

        resolver.setCookieMaxAge(60 * 60);

        return resolver;
    }
}