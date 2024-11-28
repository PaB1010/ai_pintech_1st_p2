package org.koreait.global.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file.upload")
public class FileProperties {

    // application.yml 파일 안에 있는 파일 설정의 url과 path에 접근
    private String path;
    private String url;
}