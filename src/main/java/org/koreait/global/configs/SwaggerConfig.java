package org.koreait.global.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API 설정
 *
 * - API 문서 자동 생성 도구
 *
 */
// 제목 & 설명
@OpenAPIDefinition(info = @Info(title = "포켓몬 도감 API", description = "/api/file - FILE API"))
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi openApiGroup() {

        return GroupedOpenApi.builder()
                .group("포켓몬 도감 API") // Group 이름 -> group("설명")
                .pathsToMatch("/api/**") // 경로 패턴 지정 (api 에 속하는 모든 경로)
                .build();
    }
}