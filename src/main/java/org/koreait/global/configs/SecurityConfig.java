package org.koreait.global.configs;

import org.koreait.member.libs.MemberUtil;
import org.koreait.member.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security 설정
 *
 */
@Configuration
@EnableMethodSecurity // Controller 내의 요청 메서드 단위로 권한 통제 가능
public class SecurityConfig {

    // RememberMe 주입용
    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private MemberUtil memberUtil;
    
    /**
     *
    내부적으로 생성된 Filter(로그인 계정 정보)의
    앞이나 뒤로 사용자 정의 Filter를 Chain가능
    
    HttpSecurity < 주 설정
     */
    // Spring 관리 @Bean 필수
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MemberUtil memberUtil) throws Exception {

        /**
         * Spring Security가 모르는 부분들 설정 S
         *
         *  1) login.html에서 email, password를 사용한다는 것
         *  2) 로그인 성공시 & 실패시 연산 처리
         */

        /* 인증 설정 S - 로그인 & 로그아웃
            - DSL (람다, 도메인 특화)*/

        http.formLogin(c -> {

            // 로그인 값을 넘길 곳, 로그인 양식을 처리할 주소
            c.loginPage("/member/login")
                    .usernameParameter("email") // 유저 ID로 사용할 값
                    .passwordParameter("password") // 유저 비밀번호로 사용할 값
                    /*
                    이 두가지는 거의 사용하지 않고 Handler 사용해 상세 처리를 함

                    .failureUrl("/member/login?error=1") // 로그인 실패시
                    .defaultSuccessUrl("/"); // 로그인 성공시
                     */
                    .failureHandler(new LoginFailureHandler())
                    .successHandler(new LoginSuccessHandler());
        });

        // 로그아웃시 어디로 갈 것인지 & 자세한 처리
        http.logout(c -> {
            c.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/member/login");
        });

        /* 인증 설정 E - 로그인 & 로그아웃 */

        /* 인가 설정 S - 페이지 접근 통제 */
        /**
         * authenticated() : 인증받은 사용자만 접근
         * anonymous() : 인증 받지 않은 사용자만 접근
         * permitAll() : 모든 사용자가 접근 가능
         * hasAuthority("권한 명칭") : 하나의 권한을 체크
         * hasAnyAuthority("권한1", "권한2", ...) : 나열된 권한 중 하나라도 충족하면 접근 가능
         * ROLE
         * ROLE_명칭
         * hasRole("명칭")
         * hasAnyRole(...)
         */
        http.authorizeHttpRequests(c -> {
            c.requestMatchers("/mypage/**").authenticated() // 인증한 회원만 접근 가능
                    .requestMatchers("/member/login", "/member/join", "/member/agree").anonymous() // 미인증 회원만 접근 가능
                    .requestMatchers("/admin/**").hasAnyAuthority("MANAGER", "ADMIN") // 관리자 페이지는 MANAGER, ADMIN 권한만 접근 가능
                    .anyRequest().permitAll(); // 나머지 페이지는 모두 접근 가능
        });

        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(new MemberAuthenticationExceptionHandler())  // 미로그인시 인가 실패
                    .accessDeniedHandler(new MemberAccessDeniedHandler()); // 로그인 이후 인가 실패
        });

        /* 인가 설정 E */

        /* 자동 로그인 설정 S */

        http.rememberMe(c -> {

            // 사용할 Parameter 명칭 알려줌
            // default 값 -> "remember-me"
            c.rememberMeParameter("autoLogin")
                    // 자동 로그인 유지 시간 설정
                    // default 값 -> 14일, 30일 임의 설정
                    .tokenValiditySeconds(60 * 60 * 24 * 30)
                    // 조회할 것이 무엇인지
                    .userDetailsService(memberInfoService)
                    // 로그인성공시 콜백
                    .authenticationSuccessHandler(new LoginSuccessHandler());

        });

        /* 자동 로그인 설정 E */

        /* Spring Security가 모르는 부분들 설정 E */

        // 설정 객체를 빌드로 만들어서 내보내는 역할
        return http.build();
    }

    // 단방성 암호화(복호화 불가능) -> 유동 해시(같은 값이어도 매번 해시 값이 다름)
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}