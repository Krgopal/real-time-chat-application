package com.demo.service.impl;

import com.demo.client.NotificationClient;
import com.demo.model.NotificationDeliveryStatus;
import com.demo.model.NotificationDetails;
import com.demo.model.UndeliveredMessage;
import com.demo.repository.NotificationDetailsDao;
import com.demo.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationClient notificationClient;
    private final NotificationDetailsDao notificationDetailsDao;

    @Autowired
    public NotificationServiceImpl(NotificationClient notificationClient, NotificationDetailsDao notificationDetailsDao) {
        this.notificationClient = notificationClient;
        this.notificationDetailsDao = notificationDetailsDao;
    }
    @Override
    public void sendNotification(UndeliveredMessage undeliveredMessage) {
        NotificationDetails notificationDetails = new NotificationDetails(undeliveredMessage.getChatMessageDetails());
        try {
            notificationDetailsDao.createNotificationDetails(notificationDetails);
            notificationClient.sendNotification(undeliveredMessage.getReceiver(), undeliveredMessage.getChatMessageDetails());
//            notificationDetails.setStatus(NotificationDeliveryStatus.DELIVERED);
            notificationDetailsDao.updateNotificationDetails(notificationDetails);
        } catch (Exception e) {
            LOG.error("Failed to send notification to user: " + undeliveredMessage.getReceiver() + " with conversationId: "
                    + undeliveredMessage.getChatMessageDetails().getConversationId() + " and Message id: "
                    + undeliveredMessage.getChatMessageDetails().getMessageId() + " with error message: " + e.getMessage()
                    + " and Exception: " + e.getStackTrace());
        }
    }
}
