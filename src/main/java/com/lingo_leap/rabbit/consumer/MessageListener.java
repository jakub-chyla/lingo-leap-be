package com.lingo_leap.rabbit.consumer;

import com.lingo_leap.sse.SseService;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageListener {

    private final SseService sseService;

    public MessageListener(SseService sseService) {
        this.sseService = sseService;
    }

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(List<ReportDto> dto) {
        System.out.println("Rabbit received: " + dto);

        sseService.sendReport(dto);
    }

}