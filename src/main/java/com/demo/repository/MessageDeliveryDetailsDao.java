package com.demo.repository;

import com.demo.model.MessageDeliveryDetails;

import java.util.List;

public interface MessageDeliveryDetailsDao {
    MessageDeliveryDetails createMessageDeliveryDetails(MessageDeliveryDetails messageDeliveryDetails);
    List<MessageDeliveryDetails> createMessageDeliveryDetailsInBatch(List<MessageDeliveryDetails> messageDeliveryDetails);
}
