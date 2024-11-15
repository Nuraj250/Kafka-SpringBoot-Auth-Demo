package com.example.kafka.controller;

import com.example.kafka.model.LogMessage;
import com.example.kafka.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final KafkaService kafkaService;

    @Autowired
    public LogController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @PostMapping
    public String sendLog(@RequestBody LogMessage logMessage) {
        kafkaService.sendLogMessage(logMessage);
        return "Log message sent to Kafka";
    }
}
