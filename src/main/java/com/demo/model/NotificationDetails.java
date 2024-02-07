package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.demo.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "notification_details")
public class NotificationDetails {
    @DynamoDBHashKey(attributeName = "conversation_id")
    private String conversationId;
    @DynamoDBRangeKey(attributeName = "message_id")
    @DynamoDBAttribute(attributeName = "message_id")
    private String messageId;
    @DynamoDBAttribute(attributeName = "receiver_id")
    private String receiverId;
    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "status")
    private NotificationDeliveryStatus status;
    @DynamoDBAttribute(attributeName = "created_at")
    private String createdAt;
    @DynamoDBAttribute(attributeName = "updated_at")
    private String updatedAt;

    public NotificationDetails(ChatMessageDetails chatMessageDetails) {
        this.conversationId = chatMessageDetails.getConversationId();
        this.messageId = CommonUtils.generateCompositeMessageId(chatMessageDetails.getMessageId(),
                chatMessageDetails.getSenderId(), chatMessageDetails.getMessageTo());
        this.receiverId = chatMessageDetails.getMessageTo();
        this.status = NotificationDeliveryStatus.PENDING;
        this.createdAt = chatMessageDetails.getCreatedAt();
    }
}
