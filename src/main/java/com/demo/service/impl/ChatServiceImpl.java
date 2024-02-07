package com.demo.service.impl;

import com.amazonaws.util.CollectionUtils;
import com.google.gson.Gson;
import com.demo.business.ChatMessageBO;
import com.demo.constants.CommonConstants;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.*;
import com.demo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    private final MessageService messageService;
    private final MessageDeliveryService messageDeliveryService;
    private final RedisService redisService;
    private final ServerToServerCommunicationService serverToServerCommunicationService;
    private final ExecutorService serverToServerExecutorService;
    private final MessageDeliveryDetailsService messageDeliveryDetailsService;
    private final Gson gson;

    @Autowired
    public ChatServiceImpl(MessageService messageService, MessageDeliveryService messageDeliveryService,
                           RedisService redisService, ServerToServerCommunicationService serverToServerCommunicationService,
                           MessageDeliveryDetailsService messageDeliveryDetailsService) {
        this.messageService = messageService;
        this.messageDeliveryService = messageDeliveryService;
        this.redisService = redisService;
        this.serverToServerCommunicationService = serverToServerCommunicationService;
        this.messageDeliveryDetailsService = messageDeliveryDetailsService;
        this.gson = new Gson();
        //TODO:- read from config
        this.serverToServerExecutorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        DeliveryMessageDetails deliveryMessageDetails;
        try {
            ChatMessageBO chatMessageBO = gson.fromJson(message.getPayload().toString(), ChatMessageBO.class);
            deliveryMessageDetails = messageService.processMessage(chatMessageBO);
            ChatMessageDetails chatMessageDetails = new ChatMessageDetails(deliveryMessageDetails.getMessageDetails());
            sendMessage(chatMessageDetails, deliveryMessageDetails.getReceivers());
            // TODO:- send ack.
        } catch (Exception e) {
            LOG.error("Failed to handle message: " + message + " with Exception message: " + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
//            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to handle message");
        }
    }

    public void sendMessage(ChatMessageDetails chatMessageDetails, List<String> receivers) {
        if (receivers.contains(chatMessageDetails.getSenderId())) {
            receivers.remove(chatMessageDetails.getSenderId());
        }
        Map<String, Set<String>> userServerMap = new HashMap<>();
        List<UserConnectionDetails> userConnectionDetailsList = redisService.getUserConnectionDetailsForMultipleUsers(receivers);
        LOG.info("userConnectionDetails: " + userConnectionDetailsList) ;
        if (userConnectionDetailsList!= null && !(userConnectionDetailsList.isEmpty())) {
            userServerMap = userConnectionDetailsList.stream()
                    .collect(Collectors.groupingBy(
                            UserConnectionDetails::getServerId,
                            Collectors.mapping(UserConnectionDetails::getUsername, Collectors.toSet())
                    ));
        }
        Set<String> offlineUsers = findOfflineUsers(userConnectionDetailsList, new HashSet<>(receivers));
        String currentServerId = System.getenv(CommonConstants.SERVER_ID);
        Set<String> currentServerUsers = new HashSet<>(offlineUsers);
        if (userServerMap.containsKey(currentServerId)) {
            currentServerUsers.addAll(userServerMap.get(currentServerId));
        }
        currentServerUsers.addAll(offlineUsers);
        messageDeliveryService.sendMessageToUsers(chatMessageDetails, currentServerUsers);
        userServerMap.remove(currentServerId);
        sendRequestToOtherServersForMessageDelivery(userServerMap, chatMessageDetails);
    }

    public Set<String> findOfflineUsers(List<UserConnectionDetails>userConnectionDetailsList, Set<String> receivers) {
        if (CollectionUtils.isNullOrEmpty(userConnectionDetailsList)) {
            return receivers;
        }
        for (UserConnectionDetails userConnectionDetails : userConnectionDetailsList) {
            receivers.remove(userConnectionDetails.getUsername());
        }
        return receivers;
    }

    public void sendPendingMessages(WebSocketSession session) {
        try {
            PendingMessage pendingMessage = redisService.getPendingMessageForUser(Objects.requireNonNull(session.getPrincipal()).getName());
            if (pendingMessage != null) {
                List<ChatMessageBO> undeliveredMessages = new ArrayList<>();
                for (ChatMessageDetails chatMessageDetails : pendingMessage.getMessages()) {
                    undeliveredMessages.add(new ChatMessageBO(chatMessageDetails));
                }
                WebSocketMessage<?> message = new TextMessage(gson.toJson(undeliveredMessages));
                session.sendMessage(message);
                messageDeliveryDetailsService.createDeliveredMessageDetailsForUser(pendingMessage.getMessages(), pendingMessage.getUsername());
                redisService.deletePendingMessageForUser(pendingMessage.getUsername());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRequestToOtherServersForMessageDelivery(Map<String, Set<String>> userServerMap, ChatMessageDetails chatMessageDetails) {
        for (Map.Entry<String, Set<String>> entry : userServerMap.entrySet()) {
            ServerToServerMessageDeliveryDetails messageDetails = ServerToServerMessageDeliveryDetails.builder()
                    .chatMessageDetails(chatMessageDetails)
                    .receivers(entry.getValue())
                    .build();
            serverToServerExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    serverToServerCommunicationService.sendRequestToServerForMessageDelivery(entry.getKey(), messageDetails);
                }
            });
        }
    }

}
