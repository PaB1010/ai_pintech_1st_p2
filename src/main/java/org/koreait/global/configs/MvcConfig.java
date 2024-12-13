package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC Framework 설정 - implements WebMvcConfigurer 필수!
 */

// ★ 상시 Demon Thread 로 돌아가면 Server 에 무리 가기때문에 자동 설정 ★
@EnableJpaAuditing
@EnableScheduling // @Scheduled 활성화
@EnableRedisHttpSession // ★ Session 이 Redis 에 저장할 수 있도록 기본 설정 ★
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 정적 경로 설정, 주로 CSS & JS & Image
     *
     * resource.static package 정적 경로로 설정
     *
     * HandlerMapping 이 못찾으면 마지막으로 이곳을 찾게 됨
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Ant 패턴 ** = 하위 경로를 포함한 모든 경로
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // ★ classpath : Class File 인식 가능한 경로 ★
    }

    /**
     * PATCH & PUT & DELETE 등등에서 사용
     * PATCH METHOD 로 요청 보내는 경우
     *
     * <form method='POST' ...>
     *      <input type = 'hidden' name = '_method' value = 'PATCH'>
     * </form>
     *
     * ★ Body Data 유무때문에 GET 이 아닌  form method = POST ★
     *
     * @return
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {

        return new HiddenHttpMethodFilter();
    }

    /**
     * 주소와 Template 만 이용해서 Mapping
     *
     * 단순 단면 홈페이지 설정시 사용
     *
     * @param registry
     */
    /*
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
    }
     */

    /**
     * View 를 찾는 ViewResolver 설정
     *
     * @param registry
     */
    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        WebMvcConfigurer.super.configureViewResolvers(registry);
    }
     */
}