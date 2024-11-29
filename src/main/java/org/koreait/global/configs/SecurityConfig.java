package org.koreait.global.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 *
 */
@Configuration
public class SecurityConfig {
    
    /*
    내부적으로 생성된 Filter(로그인 계정 정보)의
    앞이나 뒤로 사용자 정의 Filter를 Chain가능
    
    HttpSecurity < 주 설정
     */
    // Spring 관리 @Bean 필수
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     
        // 설정 무력화
        return http.build();
    }

    // 단방성 암호화(복호화 불가능) -> 유동 해시(같은 값이어도 매번 해시 값이 다름)
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}