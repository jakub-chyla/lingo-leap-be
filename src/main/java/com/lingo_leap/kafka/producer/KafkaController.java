package com.lingo_leap.kafka.producer;

import com.lingo_leap.rabbit.consumer.MessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;

@RestController
@RequiredArgsConstructor
public class KafkaController {


    private final KafkaTemplate<String, Book> kafkaTemplate;
    private final MessageListener messageListener;

    private static final String TOPIC = "NewTopic";

    @PostMapping("/publish")
    public void publishMessage(@RequestBody Book book) {
        kafkaTemplate.send(TOPIC, book);
        System.out.println("Kafka sent: " + book);
    }
}
