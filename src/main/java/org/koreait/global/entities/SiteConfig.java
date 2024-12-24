package org.koreait.global.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 전역 유지 Data Class
 *
 */
@Data
// 이곳에 정의되지 않은 것이 추가돼도 문제 없도록
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteConfig {

    private String siteTitle;

    private String description;

    private String keywords;

    private int cssVersion;

    private int jsVersion;

    private boolean useEmailAuth;
}
