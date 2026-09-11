package com.lingo_leap;

import com.lingo_leap.dto.AttachmentDTO;
import com.lingo_leap.dto.WordDto;
import com.lingo_leap.enums.Language;
import com.lingo_leap.model.Word;
import com.lingo_leap.repository.WordRepository;
import com.lingo_leap.service.AttachmentService;
import com.lingo_leap.service.WordService;
import com.lingo_leap.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class WordServiceTest {

    @InjectMocks
    private WordService wordService;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapWordsNoAttachments() {
        // Given
        Word word = new Word();
        word.setId(1L);
        word.setEnglish("Hello");
        word.setPolish("Cześć");

        // When
        WordDto result = Mapper.mapWordsNoAttachments(word);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("Hello", result.getEnglish());
        assertEquals("Cześć", result.getPolish());
    }

    @Test
    void testMapWordWithAttachmentsName() {
        // Given
        Word word = new Word(1L, "Cześć", "Hello");

        AttachmentDTO attachment1 = new AttachmentDTO(1L, "hello.mp3", 1L, Language.ENGLISH);
        AttachmentDTO attachment2 = new AttachmentDTO(2L, "czesc.mp3", 1L, Language.POLISH);
        List<AttachmentDTO> attachments = List.of(attachment1, attachment2);

        // When
        WordDto result = Mapper.mapWordWithAttachmentsName(word, attachments);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("Hello", result.getEnglish());
        assertEquals("Cześć", result.getPolish());
        assertEquals("hello.mp3", result.getEnglishAttachment().getFileName());
        assertEquals("czesc.mp3", result.getPolishAttachment().getFileName());
    }

    @Test
    void testGetRandomWords() {
        //given
        Word word = new Word(1L, "Hello", "Cześć");
        List<Word> words = List.of(word);

        AttachmentDTO attachment1 = new AttachmentDTO(1L, "hello.mp3", 1L, Language.ENGLISH);
        AttachmentDTO attachment2 = new AttachmentDTO(2L, "czesc.mp3", 1L, Language.POLISH);
        List<AttachmentDTO> attachments = List.of(attachment1, attachment2);

        //mock the calls
        when(attachmentService.findAll()).thenReturn(attachments);
        when(wordRepository.findAllByOrderByEnglishAsc()).thenReturn(words);

        //when
        List<WordDto> response = wordService.findAll();

        //then
        assertEquals(1L, response.get(0).getId());
        assertEquals("czesc.mp3", response.get(0).getPolishAttachment().getFileName());

    }

}
