package org.koreait.member.social.entities;

import lombok.Data;

@Data
public class SocialConfig {

    private boolean useKakaoLogin;

    private String kakaoRestApiKey;

//    private boolean useNaverLogin;
//
//    private String naverRestApiKey;

    // 지도 연동 추가시 자바스크립트 키 추가
}