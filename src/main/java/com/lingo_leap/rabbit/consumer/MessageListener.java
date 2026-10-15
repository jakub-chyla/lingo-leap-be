package com.lingo_leap.rabbit.consumer;

import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

@Component
public class MessageListener {

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(List<ReportDto> dto) {
        System.out.println("rabbit received: " + dto);
    }

}
