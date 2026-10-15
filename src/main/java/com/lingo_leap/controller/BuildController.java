package com.lingo_leap.controller;

import com.lingo_leap.kafka.producer.Book;
import com.lingo_leap.service.StartUpTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {
    private final KafkaTemplate<String, Book> kafkaTemplate;

    private final StartUpTimeService startUpTimeService;

    private static final String TOPIC = "NewTopic";

    @GetMapping
    LocalDateTime getLastBuildTime(){
        return startUpTimeService.getStartupTime();
    }

    // TODO: to remove
    @GetMapping("/kafka-test")
    String kafkaTest(){
        Book book = new Book("Clean Code", "9780132350883");
        kafkaTemplate.send(TOPIC, book);
        return "Published Successfully!";
    }
}
