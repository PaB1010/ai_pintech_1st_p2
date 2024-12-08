package org.koreait.global.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

/**
 * 공용으로 많이 쓰일
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

    @Lazy
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        // 매칭 안되는 타입(자료형이 일치하지 않을 경우)은 PASS 하는 설정
        // mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD).setSkipNullEnabled(true);
        mapper.getConfiguration().setSkipNullEnabled(true);

        return mapper;
    }
}