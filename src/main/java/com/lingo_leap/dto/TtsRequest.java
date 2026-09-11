package com.lingo_leap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TtsRequest {
    private String text;
    private String voice;
    private String rate;
    private String pitch;
}
