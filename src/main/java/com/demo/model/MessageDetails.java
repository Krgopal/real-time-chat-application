package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.demo.business.ChatMessageBO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "message_details")
public class MessageDetails {
    @DynamoDBHashKey(attributeName = "conversation_id")
    private String conversationId;
    @DynamoDBRangeKey(attributeName = "message_id")
    @DynamoDBAttribute(attributeName = "message_id")
    private String messageId;
    @DynamoDBAttribute(attributeName = "sender_id")
    private String senderId;
    @DynamoDBAttribute(attributeName = "sender_display_name")
    private String senderDisplayName;
    @DynamoDBAttribute(attributeName = "message_to")
    private String messageTo;
    @DynamoDBAttribute(attributeName = "content")
    private String content;
    @DynamoDBAttribute(attributeName = "conversation_type")
    @DynamoDBTypeConvertedEnum
    private ConversationType conversationType;
    @DynamoDBAttribute(attributeName = "status")
    @DynamoDBTypeConvertedEnum
    private MessageStatus status;
    @DynamoDBAttribute(attributeName = "created_at")
    private String createdAt;

    public MessageDetails(ChatMessageBO chatMessageBO) {
        this.messageId = chatMessageBO.getMessageId() + "_" + chatMessageBO.getSenderId();
        this.messageTo = chatMessageBO.getMessageTo();
        this.senderId = chatMessageBO.getSenderId();
        this.senderDisplayName = chatMessageBO.getSenderDisplayName();
        this.content = chatMessageBO.getContent();
        this.createdAt = chatMessageBO.getCreatedAt();
    }
}
