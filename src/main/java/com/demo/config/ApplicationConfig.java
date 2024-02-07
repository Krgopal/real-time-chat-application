package com.demo.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
public class ApplicationConfig {
    @Value("${dynamoDB.url}")
    private String dynamoDBUrl;
    @Value("${dynamoDB.username}")
    private String dynamoDbUserName;
    @Value("${dynamoDB.password}")
    private String dynamoDbPassword;
    @Value("${dynamoDB.region}")
    private String region;
    @Value("${spring.redis.host}")
    private String redisUrl;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${serverToServerAuthToken}")
    private String serverToServerAuthToken;
    @Value("${spring.kafka.consumer.topic}")
    private String undeliveredMessageTopic;
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
}
