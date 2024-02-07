package com.demo.service;

import com.demo.business.ChatMessageBO;
import com.demo.model.DeliveryMessageDetails;
import com.demo.model.MessageDetails;
import com.demo.model.MessageHistoryRequest;

import java.util.List;

public interface MessageService {
    List<ChatMessageBO> fetchOlderChatMessages(MessageHistoryRequest messageHistoryRequest);
    DeliveryMessageDetails processMessage(ChatMessageBO chatMessageBO);
    void  updateMessageDetails(MessageDetails messageDetails);
}
