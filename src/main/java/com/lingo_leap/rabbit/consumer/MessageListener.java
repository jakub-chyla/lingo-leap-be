package com.lingo_leap.rabbit.consumer;

import com.lingo_leap.kafka.producer.Book;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class MessageListener {

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(Book book) {
        System.out.println("rabbit received: " + book);
    }

}
