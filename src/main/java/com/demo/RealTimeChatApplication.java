package com.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RealTimeChatApplication {
    public static void main(String[] args) {
        LOG.info("Starting application...");
        SpringApplication.run(RealTimeChatApplication.class, args);
    }
}