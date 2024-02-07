package com.demo.service.impl;

import com.demo.model.ChatMessageDetails;
import com.demo.model.DeliveryMessageDetails;
import com.demo.model.MessageDeliveryDetails;
import com.demo.model.MessageDeliveryStatus;
import com.demo.repository.MessageDeliveryDetailsDao;
import com.demo.service.MessageDeliveryDetailsService;
import com.demo.utils.CommonUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageDeliveryDetailsServiceImpl implements MessageDeliveryDetailsService {
    private final MessageDeliveryDetailsDao messageDeliveryDetailsDao;

    @Autowired
    public MessageDeliveryDetailsServiceImpl(MessageDeliveryDetailsDao messageDeliveryDetailsDao) {
        this.messageDeliveryDetailsDao = messageDeliveryDetailsDao;
    }
    public MessageDeliveryDetails createMessageDeliveryDetails(MessageDeliveryDetails messageDeliveryDetails){
        messageDeliveryDetails.setDeliveryTime(CommonUtils.getCurrentISTDateTimeInString());
        return messageDeliveryDetailsDao.createMessageDeliveryDetails(messageDeliveryDetails);
    }

    public void createDeliveredMessageDetails(ChatMessageDetails chatMessageDetails, String receiver) {
        MessageDeliveryDetails messageDeliveryDetails= new MessageDeliveryDetails(chatMessageDetails);
        messageDeliveryDetails.setReceiver(receiver);
        messageDeliveryDetails.setStatus(MessageDeliveryStatus.DELIVERED);
        createMessageDeliveryDetails(messageDeliveryDetails);
    }

    public void createDeliveredMessageDetailsForUser(List<ChatMessageDetails> chatMessageDetailsList, String receiver) {
        List<MessageDeliveryDetails> deliveryMessageDetailsList = new ArrayList<>();
        for (ChatMessageDetails chatMessageDetails : chatMessageDetailsList) {
            MessageDeliveryDetails messageDeliveryDetails= new MessageDeliveryDetails(chatMessageDetails);
            messageDeliveryDetails.setReceiver(receiver);
            messageDeliveryDetails.setStatus(MessageDeliveryStatus.DELIVERED);
            messageDeliveryDetails.setDeliveryTime(CommonUtils.getCurrentISTDateTimeInString());
            deliveryMessageDetailsList.add(messageDeliveryDetails);
        }
        messageDeliveryDetailsDao.createMessageDeliveryDetailsInBatch(deliveryMessageDetailsList);
    }

}
