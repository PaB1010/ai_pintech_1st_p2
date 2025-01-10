package org.koreait.member.social.services;

public interface SocialLoginService {

    String getToken(String code);

    // 접근 Token 아니고 Kakao ID
    boolean login(String token);
}