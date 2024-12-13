package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * File 설정
 *
 * FileUpload와 연결할 정적 경로 설정
 */
@Configuration
@RequiredArgsConstructor
// ★ 의존 주입 위해 가져옴 ★
@EnableConfigurationProperties(FileProperties.class)
public class FileConfig implements WebMvcConfigurer {
    
    // File 설정 주입, 주로 정적 경로
    private final FileProperties properties;

    // ★ Spring Web Mvc - Browser 에서 접근 가능하게 정적 경로(URL) 설정 ★
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        registry.addResourceHandler(properties.getUrl() + "**")
                .addResourceLocations("file:///" + properties.getPath());
    }
}