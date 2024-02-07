package com.demo.service;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface SessionService {
    void addNewConnection(WebSocketSession session);
    void afterConnectionClosed(WebSocketSession session, CloseStatus status);

    WebSocketSession getWebSocketSessionByUsername(String username);
}
