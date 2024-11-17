// MVC Framework 설정 - implements WebMvcConfigurer 필수!

package org.koreait.global.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /*
    정적 경로 설정, 주로 CSS & JS & Image

    resource.static package 정적 경로로 설정

    HandlerMapping이 못찾으면 마지막으로 이곳을 찾게 됨
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Ant 패턴 ** = 하위 경로를 포함한 모든 경로
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}