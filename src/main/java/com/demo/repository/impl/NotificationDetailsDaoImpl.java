package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.model.NotificationDetails;
import com.demo.repository.NotificationDetailsDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationDetailsDaoImpl implements NotificationDetailsDao {
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public NotificationDetailsDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
    @Override
    public NotificationDetails createNotificationDetails(NotificationDetails notificationDetails) {
        DateTime timestamp = new DateTime();
        notificationDetails.setCreatedAt(timestamp.toString());
        notificationDetails.setUpdatedAt(timestamp.toString());
        dynamoDBMapper.save(notificationDetails);
        return notificationDetails;
    }

    @Override
    public NotificationDetails updateNotificationDetails(NotificationDetails notificationDetails) {
        DateTime updatedTime = new DateTime();
        notificationDetails.setUpdatedAt(updatedTime.toString());
        Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
        expectedAttributeValueMap.put("conversation_id", new ExpectedAttributeValue(new AttributeValue().withS(notificationDetails.getConversationId())));
        expectedAttributeValueMap.put("message_id", new ExpectedAttributeValue(new AttributeValue().withS(String.valueOf(notificationDetails.getMessageId()))));
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
        dynamoDBMapper.save(notificationDetails, saveExpression);
        return notificationDetails;
    }
}
