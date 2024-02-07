package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.demo.exception.CommonExceptionType;
import com.demo.model.MessageDetails;
import com.demo.repository.MessageDetailsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MessageDetailsDaoImpl implements MessageDetailsDao {
    private final DynamoDBMapper dynamoDbMapper;

    @Autowired
    public MessageDetailsDaoImpl(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }
    @Override
    public MessageDetails createMessage(MessageDetails messageDetails) {
        dynamoDbMapper.save(messageDetails);
        return messageDetails;
    }

    @Override
    public List<MessageDetails> fetchHistory(String conversationId) {
        try {
            MessageDetails hashKeyValues = new MessageDetails();
            hashKeyValues.setConversationId(conversationId);
            DynamoDBQueryExpression<MessageDetails> queryExpression = new DynamoDBQueryExpression<>();
            queryExpression.withHashKeyValues(hashKeyValues);
            queryExpression.setScanIndexForward(false);
            System.out.println();
            return dynamoDbMapper.query(MessageDetails.class, queryExpression);
        }catch (Exception e) {
            LOG.error("Failed to fetch history for conversationId: " + conversationId + " with Exception message: "
                    + e.getMessage() + " and Exception: " + e.getStackTrace());
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to fetch history oof conversation");
        }
    }

    public MessageDetails updateMessageDetails(MessageDetails messageDetails) {
        try {
            Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
            expectedAttributeValueMap.put("conversation_id", new ExpectedAttributeValue(new AttributeValue().withS(messageDetails.getConversationId())));
            expectedAttributeValueMap.put("message_id", new ExpectedAttributeValue(new AttributeValue().withS(messageDetails.getMessageId())));
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
            dynamoDbMapper.save(messageDetails, saveExpression);
            return messageDetails;
        } catch (Exception e) {
            LOG.error("Failed to update message details: " + messageDetails.getConversationId() + " with error message: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("message details update failed");
        }
    }
}
