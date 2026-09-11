package com.lingo_leap.dto;

import lombok.Data;

@Data
public class WordDto {
    private Long id;
    private String polish;
    private AttachmentDTO polishAttachment;
    private String english;
    private AttachmentDTO englishAttachment;

}
