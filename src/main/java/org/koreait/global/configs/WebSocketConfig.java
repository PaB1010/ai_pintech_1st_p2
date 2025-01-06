package org.koreait.global.configs;

import org.koreait.message.websockets.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MessageHandler messageHandler; // 직접 정의한 Handler Class

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // prod 환경 변수에서 가져와 도메인 설정
        String profile = System.getenv("spring.profiles.active");

        // ws://(도메인)localhost:3000/message
        registry.addHandler(messageHandler, "/msg")
                // 허용할 도메인, 여러개 가능
                // .setAllowedOrigins("http://joinfar.xzy:3000", "https://joinfar.xzy:4000");
                // 개발시 : localhost
                // 실제 배포시 : 지정한 도메인 허용
                .setAllowedOrigins(profile.contains("prod") ? "" : "http://localhost:3000");
    }
}