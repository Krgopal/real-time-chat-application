package com.demo.client.impl;

import com.demo.client.NotificationClient;
import com.demo.model.ChatMessageDetails;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientImpl implements NotificationClient {
    @Override
    public void sendNotification(String receiver, ChatMessageDetails payload) {
        System.out.println("Sending notification to user: " + receiver + " with message: " + payload);
    }
}
