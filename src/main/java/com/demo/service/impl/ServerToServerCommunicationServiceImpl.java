package com.demo.service.impl;

import com.demo.business.ChatMessageBO;
import com.demo.client.ServerToServerCommunicationClient;
import com.demo.exception.CommonExceptionType;
import com.demo.exception.CustomException;
import com.demo.model.*;
import com.demo.producer.KafkaProducer;
import com.demo.service.MessageDeliveryService;
import com.demo.service.RedisService;
import com.demo.service.ServerToServerCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Service
public class ServerToServerCommunicationServiceImpl implements ServerToServerCommunicationService {
    private final MessageDeliveryService messageDeliveryService;
    private final ServerToServerCommunicationClient serverToServerCommunicationClient;
    private final KafkaProducer kafkaProducer;
    private final RedisService redisService;

    public ServerToServerCommunicationServiceImpl(MessageDeliveryService messageDeliveryService,
                                                  ServerToServerCommunicationClient serverToServerCommunicationClient,
                                                  KafkaProducer kafkaProducer,
                                                  RedisService redisService) {
        this.messageDeliveryService = messageDeliveryService;
        this.serverToServerCommunicationClient = serverToServerCommunicationClient;
        this.kafkaProducer = kafkaProducer;
        this.redisService = redisService;
    }

    public void sendMessageToUsers(ServerToServerMessageDeliveryDetails serverToServerMessageDeliveryDetails) {
        try {
            sendMessage(serverToServerMessageDeliveryDetails.getChatMessageDetails(), serverToServerMessageDeliveryDetails.getReceivers());
        } catch (CustomException ce) {
            LOG.error("Failed to send server communication to send message: " + serverToServerMessageDeliveryDetails + " with Exception: " + ce);
            throw ce;
        } catch (Exception e) {
            LOG.error("Failed to send server communication to send message: " + serverToServerMessageDeliveryDetails + " with Exception message: "
                    + e.getMessage() + " and Exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to process request.");
        }
    }

    public void sendMessage(ChatMessageDetails chatMessageDetails, Set<String> receivers) {
        messageDeliveryService.sendMessageToUsers(chatMessageDetails, receivers);
    }

    public void sendRequestToServerForMessageDelivery(String serverId, ServerToServerMessageDeliveryDetails messageDetails) {
        try {
            LOG.info("sending server communication");
            boolean successful = serverToServerCommunicationClient.sendRequestToServerForMessageDelivery(serverId, messageDetails);
            if (successful) {
                return;
            }
        } catch (Exception e) {
            LOG.error("Failed to send server communication to server: " + serverId + " with Exception message: " + e.getMessage() + " and exception: " + e.getStackTrace());
        }
        LOG.info("Failed to deliver send message request to server: " + serverId + " for message: " + messageDetails);
        messageDetails.getReceivers().forEach(receiver -> processUnDeliveredMessage(receiver, messageDetails.getChatMessageDetails()));
    }

    public void processUnDeliveredMessage(String receiver, ChatMessageDetails chatMessageDetails) {
        updateUndeliveredMessageToRedis(receiver, chatMessageDetails);
        publishKafkaMessageForNotification(receiver, chatMessageDetails);
    }

    public void updateUndeliveredMessageToRedis(String receiver, ChatMessageDetails chatMessageDetails) {
        redisService.setPendingMessageForUser(receiver, PendingMessage.builder().username(receiver).messages(Arrays.asList(chatMessageDetails)).build());
    }

    public void publishKafkaMessageForNotification(String receiver, ChatMessageDetails chatMessageDetails) {
        kafkaProducer.sendUndeliveredChatMessage(UndeliveredMessage.builder()
                .receiver(receiver)
                .chatMessageDetails(chatMessageDetails).build());
    }


}
