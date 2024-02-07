package com.demo.service;

import com.demo.model.ChatMessageDetails;

import java.util.Set;

public interface MessageDeliveryService {
    void sendMessageToUsers(ChatMessageDetails chatMessageDetails, Set<String> receivers);
}
