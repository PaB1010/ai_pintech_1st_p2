// MVC Framework 설정 - implements WebMvcConfigurer 필수!

package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableJpaAuditing
@EnableScheduling // @Scheduled 활성화
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

    /**
     * PATCH & PUT & DELETE 등등에서 사용
     * PATCH METHOD 로 요청 보내는 경우
     *
     * <form method='POST' ...>
     *      <input type = 'hidden' name = '_method' value = 'PATCH'>
     * </form>
     *
     * @return
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {

        return new HiddenHttpMethodFilter();
    }
}