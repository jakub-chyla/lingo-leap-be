package com.lingo_leap.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Book {

    private String bookName;
    private String isbn;

}
