package com.lingo_leap.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryKafka {

    private String name;
    private String word;
    private Boolean answer;

}