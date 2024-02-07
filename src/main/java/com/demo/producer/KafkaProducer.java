package com.demo.producer;

import com.google.gson.Gson;
import com.demo.config.ApplicationConfig;
import com.demo.model.UndeliveredMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ApplicationConfig applicationConfig;
    private final Gson gson;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ApplicationConfig applicationConfig, Gson gson) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationConfig = applicationConfig;
        this.gson = gson;
    }

    public void sendMessage(String topic, String key,  String message) {
        try {
            kafkaTemplate.send(topic, key, message);
        } catch (Exception e) {
            LOG.error("Failed to produce kafka message");
        }
    }

    public void sendUndeliveredChatMessage(UndeliveredMessage undeliveredMessage) {
        String topic = applicationConfig.getUndeliveredMessageTopic();
        String message  = gson.toJson(undeliveredMessage);
        sendMessage(topic, undeliveredMessage.getReceiver(), message);
    }
}
