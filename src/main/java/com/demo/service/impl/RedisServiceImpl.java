package com.demo.service.impl;

import com.amazonaws.util.CollectionUtils;
import com.demo.model.ChatMessageDetails;
import com.google.gson.Gson;
import com.demo.business.ChatMessageBO;
import com.demo.client.RedisClient;
import com.demo.model.PendingMessage;
import com.demo.model.UserConnectionDetails;
import com.demo.service.RedisService;
import com.demo.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    private final RedisClient redisClient;
    private final Gson gson;
    @Autowired
    public RedisServiceImpl(RedisClient redisClient) {
        this.redisClient = redisClient;
        this.gson = new Gson();
    }

    public PendingMessage getPendingMessageForUser(String username) {
        String pendingMessageJson = redisClient.get(CommonUtils.generatePendingMessageRedisKey(username));
        return gson.fromJson(pendingMessageJson, PendingMessage.class);
    }

    public void setPendingMessageForUser(String username, PendingMessage pendingMessage) {
        PendingMessage currentMessages = getPendingMessageForUser(username);
        if (currentMessages != null && isMessageAlreadyPresent(currentMessages, pendingMessage)) {
            currentMessages.getMessages().addAll(pendingMessage.getMessages());
        } else {
            currentMessages = pendingMessage;
        }
        String pendingMessageJson = gson.toJson(currentMessages);
        redisClient.set(CommonUtils.generatePendingMessageRedisKey(username), pendingMessageJson);
    }
    public void deletePendingMessageForUser(String username) {
        redisClient.delete(CommonUtils.generatePendingMessageRedisKey(username));
    }

    public boolean isMessageAlreadyPresent(PendingMessage currentMessages, PendingMessage newMessage) {
        Optional<ChatMessageDetails> messagePresent = currentMessages.getMessages().stream()
                .filter(chatMessageBO -> chatMessageBO.getSenderId().equals(newMessage.getMessages().get(0).getSenderId())
                        && chatMessageBO.getMessageId().equals(newMessage.getMessages().get(0).getMessageId())
                        && chatMessageBO.getMessageTo().equals(newMessage.getMessages().get(0).getMessageTo()))
                .findFirst();
        return messagePresent.isPresent();
    }


    public UserConnectionDetails getUserConnectionDetails(String username) {
        String userConnectionDetailsJson = redisClient.get(CommonUtils.generateUserConnectionRedisKey(username));
        return gson.fromJson(userConnectionDetailsJson, UserConnectionDetails.class);
    }

    public List<UserConnectionDetails> getUserConnectionDetailsForMultipleUsers(List<String> usernames) {
        List<String> keys = usernames.stream().map(CommonUtils::generateUserConnectionRedisKey).collect(Collectors.toList());
        String keysString = String.join(" ", keys);
        LOG.info("keys: " + keysString);
        List<String> userConnectionDetailsJson = redisClient.mGet(keysString);
        LOG.info("userConnectionDetailsJson: " + userConnectionDetailsJson);
        if (userConnectionDetailsJson == null || !userConnectionDetailsJson.isEmpty() || Objects.equals(userConnectionDetailsJson.toString(), "[null]")) {
            return null;
        }
        return userConnectionDetailsJson.stream()
                .map(userConnectionDetails -> gson.fromJson(userConnectionDetails, UserConnectionDetails.class))
                .collect(Collectors.toList());
    }

    public void setUserConnectionDetails(UserConnectionDetails userConnectionDetails) {
        String userConnectionDetailsJson = gson.toJson(userConnectionDetails);
        redisClient.set(CommonUtils.generateUserConnectionRedisKey(userConnectionDetails.getUsername()), userConnectionDetailsJson);
    }

    public void deleteUserConnectionDetails(String username) {
        redisClient.delete(CommonUtils.generateUserConnectionRedisKey(username));
    }


}
