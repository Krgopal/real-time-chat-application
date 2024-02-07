package com.demo.model;

import com.demo.business.ChatMessageBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDetails {
    private String conversationId;
    private BigInteger messageId;
    private String senderId;
    private String senderDisplayName;
    private String messageTo;
    private String content;
    private String createdAt;

    public ChatMessageDetails(ChatMessageBO chatMessageBO) {
        this.messageId = chatMessageBO.getMessageId();
        this.senderId = chatMessageBO.getSenderId();
        this.senderDisplayName = chatMessageBO.getSenderDisplayName();
        this.messageTo = chatMessageBO.getMessageTo();
        this.content = chatMessageBO.getContent();
        this.createdAt = chatMessageBO.getCreatedAt();
    }

    public ChatMessageDetails(MessageDetails messageDetails) {
        this.conversationId = messageDetails.getConversationId();
        this.messageId = BigInteger.valueOf(Long.parseLong(messageDetails.getMessageId().split("_")[0]));
        this.senderId = messageDetails.getSenderId();
        this.senderDisplayName = messageDetails.getSenderDisplayName();
        this.messageTo = messageDetails.getMessageTo();
        this.content = messageDetails.getContent();
        this.createdAt = messageDetails.getCreatedAt();
    }
}
