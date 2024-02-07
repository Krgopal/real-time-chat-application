package com.demo.service;

import com.demo.model.PendingMessage;
import com.demo.model.UserConnectionDetails;

import java.util.List;

public interface RedisService {
    PendingMessage getPendingMessageForUser(String username);
    void setPendingMessageForUser(String username, PendingMessage pendingMessage);
    void deletePendingMessageForUser(String username);
    UserConnectionDetails getUserConnectionDetails(String username);
    List<UserConnectionDetails> getUserConnectionDetailsForMultipleUsers(List<String> usernames);
    void setUserConnectionDetails(UserConnectionDetails userConnectionDetails);
    void deleteUserConnectionDetails(String username);
}
