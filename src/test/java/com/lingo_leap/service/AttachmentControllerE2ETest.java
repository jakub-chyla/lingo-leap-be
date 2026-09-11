package com.lingo_leap.service;

import com.lingo_leap.enums.Language;
import com.lingo_leap.model.Attachment;
import com.lingo_leap.model.Word;
import com.lingo_leap.repository.AttachmentRepository;
import com.lingo_leap.repository.WordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = "cors.allowed.origins=http://localhost")
class AttachmentControllerE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @MockitoBean
    private TtsService ttsService;

    @AfterEach
    void cleanDatabase() {
        attachmentRepository.deleteAll();
        wordRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void generatesSoundsForWordsWithoutAttachments() throws Exception {
        Word word = new Word();
        word.setEnglish("hello");
        word.setPolish("czesc");
        Word savedWord = wordRepository.save(word);

        mockMvc.perform(get("/at/get-sounds-for-empty-words"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(ttsService).getSoundForEmptyWord(org.mockito.ArgumentMatchers.argThat(
                generatedWord -> generatedWord.getId().equals(savedWord.getId())
        ));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void skipsWordsThatAlreadyHaveAnAttachment() throws Exception {
        Word word = new Word();
        word.setEnglish("hello");
        word.setPolish("czesc");
        Word savedWord = wordRepository.save(word);

        Attachment attachment = new Attachment();
        attachment.setWordId(savedWord.getId());
        attachment.setFileName("hello.mp3");
        attachment.setFileType("audio/mpeg");
        attachment.setLanguage(Language.ENGLISH);
        attachment.setData(new byte[]{1});
        attachmentRepository.save(attachment);

        mockMvc.perform(get("/at/get-sounds-for-empty-words"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verifyNoInteractions(ttsService);
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/at/get-sounds-for-empty-words"))
                .andExpect(status().isUnauthorized());
    }
}
