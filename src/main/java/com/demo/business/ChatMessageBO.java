package com.demo.business;

import com.demo.model.ChatMessageDetails;
import com.demo.model.MessageDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageBO {
    private BigInteger messageId;
    private String senderId;
    private String senderDisplayName;
    private String messageTo;
    private String content;
    private String createdAt;

    public ChatMessageBO(ChatMessageDetails chatMessageDetails) {
        this.messageId = chatMessageDetails.getMessageId();
        this.messageTo = chatMessageDetails.getMessageTo();
        this.senderId = chatMessageDetails.getSenderId();
        this.senderDisplayName = chatMessageDetails.getSenderDisplayName();
        this.content = chatMessageDetails.getContent();
        this.createdAt = chatMessageDetails.getCreatedAt();
    }

    public ChatMessageBO(MessageDetails messageDetails) {
        this.messageId = BigInteger.valueOf(Long.parseLong(messageDetails.getMessageId().split("_")[0]));
        this.messageTo = messageDetails.getMessageTo();
        this.senderId = messageDetails.getSenderId();
        this.senderDisplayName = messageDetails.getSenderDisplayName();
        this.content = messageDetails.getContent();
        this.createdAt = messageDetails.getCreatedAt();
    }

}
