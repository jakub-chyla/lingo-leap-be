package com.lingo_leap.controller;

import com.lingo_leap.kafka.producer.HistoryKafka;
import com.lingo_leap.service.StartUpTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {
    private final KafkaTemplate<String, HistoryKafka> kafkaTemplate;

    private final StartUpTimeService startUpTimeService;

    private static final String TOPIC = "NewTopic";

    @GetMapping
    LocalDateTime getLastBuildTime(){
        return startUpTimeService.getStartupTime();
    }

    // TODO: to remove
    @GetMapping("/kafka-test")
    String kafkaTest(){
        HistoryKafka historyKafka = new HistoryKafka("John", "hello", true);
        kafkaTemplate.send(TOPIC, historyKafka);
        return "Published Successfully!";
    }

    @GetMapping("/kafka-test-2")
    String kafkaTest2(){
        return "kafka";
    }
}
