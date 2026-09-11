package com.lingo_leap.utils;

import com.lingo_leap.dto.AttachmentDTO;
import com.lingo_leap.dto.WordDto;
import com.lingo_leap.model.Word;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class Mapper {
    public WordDto mapWordWithAttachmentsName(Word word, List<AttachmentDTO> attachments) {
        WordDto wordDto = new WordDto();
        wordDto.setId(word.getId());
        wordDto.setEnglish(word.getEnglish());
        wordDto.setPolish(word.getPolish());

        List<AttachmentDTO> filteredAttachments = attachments.stream()
                .filter(a -> a.getWordId().equals(word.getId()))
                .sorted(Comparator.comparing(AttachmentDTO::getLanguage))
                .collect(Collectors.toList());

        if (!filteredAttachments.isEmpty()) {
            AttachmentDTO attachment = filteredAttachments.get(0);
            wordDto.setEnglishAttachment(attachment);
        }
        if (filteredAttachments.size() > 1) {
            AttachmentDTO attachment = filteredAttachments.get(1);
            wordDto.setPolishAttachment(attachment);
        }

        return wordDto;
    }

    public WordDto mapWordsNoAttachments(Word word) {
        WordDto wordDto = new WordDto();
        wordDto.setId(word.getId());
        wordDto.setEnglish(word.getEnglish());
        wordDto.setPolish(word.getPolish());

        return wordDto;
    }

}
