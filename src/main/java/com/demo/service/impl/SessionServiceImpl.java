package com.demo.service.impl;

import com.demo.exception.CommonExceptionType;
import com.demo.model.UserConnectionDetails;
import com.demo.service.RedisService;
import com.demo.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Objects;

import static com.demo.constants.CommonConstants.SERVER_ID;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final HashMap<String, WebSocketSession> connectionsMap;
    public final RedisService redisService;
    @Autowired
    public SessionServiceImpl(RedisService redisService) {
        this.redisService = redisService;
        this.connectionsMap = new HashMap<>();
    }
    @Override
    public void addNewConnection(WebSocketSession session) {
        String username = Objects.requireNonNull(session.getPrincipal()).getName();
        LOG.info("connection opened for user: " + username);
        connectionsMap.put(username, session);
        updateServerInfo(session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = Objects.requireNonNull(session.getPrincipal()).getName();
        LOG.info("connection closed for user: " + username);
        connectionsMap.remove(username);
        redisService.deleteUserConnectionDetails(username);
    }

    public WebSocketSession getWebSocketSessionByUsername(String username) {
        if (connectionsMap.containsKey(username)) {
            return connectionsMap.get(username);
        }
        throw CommonExceptionType.NOT_FOUND.getException("Connection not found.");
    }

    public void updateServerInfo(WebSocketSession session) {
        UserConnectionDetails userConnectionDetails = UserConnectionDetails.builder()
                .username(Objects.requireNonNull(session.getPrincipal()).getName())
                .serverId(System.getenv(SERVER_ID))
                .build();
        redisService.setUserConnectionDetails(userConnectionDetails);
    }
}
