package com.demo.repository;

import com.demo.model.MessageDetails;

import java.util.List;

public interface MessageDetailsDao {
    MessageDetails createMessage(MessageDetails messageDetails);
    List<MessageDetails> fetchHistory(String conversationId);
    MessageDetails updateMessageDetails(MessageDetails messageDetails);
}
