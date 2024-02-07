package com.demo.handler;

import com.demo.service.ChatService;
import com.demo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.*;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {
    private final SessionService sessionService;
    private final ChatService chatService;

    @Autowired
    public ChatWebSocketHandler(SessionService sessionService, ChatService chatService) {
        this.sessionService = sessionService;
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionService.addNewConnection(session);
        chatService.sendPendingMessages(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        chatService.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println(Objects.requireNonNull(session.getPrincipal()).getName() + " " + Arrays.toString(exception.getStackTrace()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionService.afterConnectionClosed(session, status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
