package com.demo.service;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface ChatService {
    void handleMessage(WebSocketSession session, WebSocketMessage<?> message);
    void sendPendingMessages(WebSocketSession session);
}
