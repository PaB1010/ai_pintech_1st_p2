package org.koreait.global.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * File Data Class
 */
@Data
@ConfigurationProperties(prefix = "file.upload")
public class FileProperties {

    // application.yml 설정의 url & path 주입
    private String path;
    
    private String url;
}