package com.demo.client;

import com.demo.model.ChatMessageDetails;

public interface NotificationClient {
    void sendNotification(String receiver, ChatMessageDetails payload);

}
