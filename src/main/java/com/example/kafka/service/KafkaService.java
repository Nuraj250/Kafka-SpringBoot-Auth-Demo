package com.example.kafka.service;

import com.example.kafka.model.LogMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to send log messages to Kafka
    public void sendLogMessage(LogMessage logMessage) {
        String message = String.format("Level: %s, Message: %s", logMessage.getLevel(), logMessage.getMessage());
        kafkaTemplate.send("logs-topic", message);
    }

    // Kafka consumer method
    @KafkaListener(topics = "logs-topic", groupId = "group1")
    public void consume(String message) {
        System.out.println("Consumed log message: " + message);
    }
}
