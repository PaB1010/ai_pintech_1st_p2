package org.koreait.member.social.services;

public interface SocialLoginService {

    String getToken(String code);

    // 접근 Token 아니고 Kakao ID
    boolean login(String token);
    
    // 주소 자동 완성
    String getLoginUrl(String redirectUrl);

    void connect(String token);

    void disconnect();

    boolean exists(String token);
}