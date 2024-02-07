package com.demo.service;

import com.demo.model.UndeliveredMessage;

public interface NotificationService {
    void sendNotification(UndeliveredMessage undeliveredMessage);
}
