package com.lingo_leap.dto;

import com.lingo_leap.model.Word;
import lombok.Data;

import java.util.List;

@Data
public class RandomWord {
    private Word word;
    private List<String> answers;
}
