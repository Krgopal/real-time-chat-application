package com.demo.consumer;

import com.google.gson.Gson;
import com.demo.model.UndeliveredMessage;
import com.demo.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {
    private final NotificationService notificationService;
    private final Gson gson;

    @Autowired
    public KafkaConsumer(NotificationService notificationService, Gson gson) {
        this.notificationService = notificationService;
        this.gson = gson;
    }
//    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "false")
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        LOG.info("Received message: " + message);
        UndeliveredMessage undeliveredMessage = gson.fromJson(message, UndeliveredMessage.class);
        notificationService.sendNotification(undeliveredMessage);
    }
}
