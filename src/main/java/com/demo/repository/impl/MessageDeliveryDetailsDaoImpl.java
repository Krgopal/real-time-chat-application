package com.demo.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.demo.exception.CommonExceptionType;
import com.demo.model.MessageDeliveryDetails;
import com.demo.repository.MessageDeliveryDetailsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MessageDeliveryDetailsDaoImpl implements MessageDeliveryDetailsDao {
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public MessageDeliveryDetailsDaoImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public MessageDeliveryDetails createMessageDeliveryDetails(MessageDeliveryDetails messageDeliveryDetails) {
        try {
            dynamoDBMapper.save(messageDeliveryDetails);
            return messageDeliveryDetails;
        } catch (Exception e) {
            LOG.error("Failed to save message delivery details: " + messageDeliveryDetails + " With error: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to persist data in table.");
        }
    }

    public List<MessageDeliveryDetails> createMessageDeliveryDetailsInBatch(List<MessageDeliveryDetails> messageDeliveryDetails) {
        try {
            dynamoDBMapper.batchSave(messageDeliveryDetails);
            return messageDeliveryDetails;
        } catch (Exception e) {
            LOG.error("Failed to batch save message delivery details: " + messageDeliveryDetails + " With error: " + e.getMessage()
                    + " and exception: " + Arrays.toString(e.getStackTrace()));
            throw CommonExceptionType.INTERNAL_SERVER_ERROR.getException("Failed to persist data in table.");
        }
    }
}
