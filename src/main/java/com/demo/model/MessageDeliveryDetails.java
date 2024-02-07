package com.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "message_delivery_details")
public class MessageDeliveryDetails {
    @DynamoDBHashKey(attributeName = "id")
    private String id;
    @DynamoDBRangeKey(attributeName = "receiver")
    @DynamoDBAttribute(attributeName = "receiver")
    private String receiver;
    @DynamoDBAttribute(attributeName = "status")
    @DynamoDBTypeConvertedEnum
    private MessageDeliveryStatus status;
    @DynamoDBAttribute(attributeName = "delivery_time")
    private String deliveryTime;

    public MessageDeliveryDetails(ChatMessageDetails chatMessageDetails) {
        this.id = chatMessageDetails.getConversationId() + "_" +  chatMessageDetails.getMessageId().toString()
                + "_" +  chatMessageDetails.getMessageTo();
    }
}
