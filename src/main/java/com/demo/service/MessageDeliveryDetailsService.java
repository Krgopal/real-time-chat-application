package com.demo.service;

import com.demo.model.ChatMessageDetails;

import java.util.List;

public interface MessageDeliveryDetailsService {
    void createDeliveredMessageDetails(ChatMessageDetails chatMessageDetails, String receiver);
    void createDeliveredMessageDetailsForUser(List<ChatMessageDetails> chatMessageDetails, String receiver);
}
