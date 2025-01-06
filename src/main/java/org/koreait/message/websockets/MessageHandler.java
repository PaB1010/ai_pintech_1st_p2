package org.koreait.message.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 관련 Handler
 *
 * 추상 클래스인 TextWEbSocketHandler 상속 필수
 *
 */
@Component
public class MessageHandler extends TextWebSocketHandler {

    // 접속이 될때마다 추가할 List - 단순 알람욤
    private List<WebSocketSession> sessions = new ArrayList<>();

    /**
     * 요청이 왔는지 체크
     * init 개념
     *
     * 이 Session(연결 객체) 개수를 알면 몇명이 접속중인지 알 수 있음
     * - 우리가 아는 Session 이 아니고 Web Socket 연결 통로 객체로 port 등등이 들어있음
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 브라우저 콘솔에서 const webSocket = new WebSocket("ws://localhost:3000/msg"); 시 호출

        // System.out.println("connected");

        sessions.add(session);
    }

    /**
     * 사용자가 보낸 Message 가 유입돼 거쳐가는 곳
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 브라우저 콘솔에서 webSocket.send("메세지 전송"); 시 호출

        // System.out.println("textMessage");
        // getPayload() = 유입된 Message
        // String text = message.getPayload();
        // System.out.println(text);
        // "테스트 메세지" : 브라우저 콘솔에서 입력한 메세지

        for (WebSocketSession s : sessions) {

            s.sendMessage(message);

            // 본인 것인지 조회 후 정보 재출력?
        }
    }

    /**
     * 연결이 끊겼을 때
     * destroy 개념
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결된 브라우저 닫을 경우 호출

        // System.out.println("closed");

        // 연결 객체 제거
        sessions.remove(session);

        // 오픈 되지 않은 세션은 일괄 선택해 제거 (안전하게 메모리 낭비 방지)
        sessions.stream().filter(s -> !s.isOpen()).forEach(sessions::remove);
    }
}