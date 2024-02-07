package com.demo.service.impl;

import com.demo.model.*;
import com.demo.service.MessageDeliveryDetailsService;
import com.google.gson.Gson;
import com.demo.business.ChatMessageBO;
import com.demo.exception.CustomException;
import com.demo.producer.KafkaProducer;
import com.demo.service.MessageDeliveryService;
import com.demo.service.RedisService;
import com.demo.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class MessageDeliveryServiceImpl implements MessageDeliveryService {
    private final SessionService sessionService;
    private final ExecutorService serverToUserExecutorService;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;
    private final MessageDeliveryDetailsService messageDeliveryDetailsService;
    private final Gson gson;

    public MessageDeliveryServiceImpl(SessionService sessionService, KafkaProducer kafkaProducer,
                                      RedisService redisService, MessageDeliveryDetailsService messageDeliveryDetailsService) {
        this.sessionService = sessionService;
        this.kafkaProducer = kafkaProducer;
        this.redisService = redisService;
        this.messageDeliveryDetailsService = messageDeliveryDetailsService;
        this.serverToUserExecutorService = Executors.newFixedThreadPool(10);
        this.gson = new Gson();
    }

    public void sendMessageToUsers(ChatMessageDetails chatMessageDetails, Set<String> receivers) {
        for (String receiver : receivers) {
            serverToUserExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    sendMessageToUser(chatMessageDetails, receiver);
                }
            });
        }
    }

    public void sendMessageToUser(ChatMessageDetails chatMessageDetails, String receiver) {
        ChatMessageBO chatMessageBO = new ChatMessageBO(chatMessageDetails);
        try {
            WebSocketSession session = sessionService.getWebSocketSessionByUsername(receiver);
            WebSocketMessage<?> message = new TextMessage(gson.toJson(Collections.singletonList(chatMessageBO)));
            session.sendMessage(message);
            messageDeliveryDetailsService.createDeliveredMessageDetails(chatMessageDetails, receiver);
            return;
        } catch (CustomException ce) {
            LOG.error("Failed to send message to user: " + receiver + " with exception: " + ce);
            LOG.error("user: "+ receiver + " is offline. sending notification");
        } catch (Exception e) {
            LOG.error("Failed to Deliver message to user: "+ receiver + " sending notification");
        }
        sendUndeliveredMessage(chatMessageDetails, receiver);
    }


    public void sendUndeliveredMessage(ChatMessageDetails chatMessageDetails, String receiver) {
        UndeliveredMessage undeliveredMessage = UndeliveredMessage.builder()
                .chatMessageDetails(chatMessageDetails)
                .receiver(receiver)
                .build();
        kafkaProducer.sendUndeliveredChatMessage(undeliveredMessage);
        redisService.setPendingMessageForUser(receiver, PendingMessage.builder().username(receiver).messages(Arrays.asList(chatMessageDetails)).build());
    }

}
