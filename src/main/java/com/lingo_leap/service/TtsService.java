package com.lingo_leap.service;

import com.lingo_leap.dto.TtsRequest;
import com.lingo_leap.enums.Language;
import com.lingo_leap.model.Attachment;
import com.lingo_leap.model.Word;
import com.lingo_leap.repository.AttachmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TtsService {

    private final WebClient webClient;

    private final AttachmentRepository attachmentRepository;
    private final TtsApiKeyService ttsApiKeyService;


    public TtsService(WebClient.Builder builder,
                      AttachmentRepository attachmentRepository,
                      TtsApiKeyService ttsApiKeyService) {
        this.webClient = builder
                .baseUrl("https://eidosspeech.xyz")
                .build();
        this.attachmentRepository = attachmentRepository;
        this.ttsApiKeyService = ttsApiKeyService;
    }

    public Mono<byte[]> generateSpeech(TtsRequest request) {
        return Mono.fromCallable(ttsApiKeyService::claimKey)
                .flatMap(apiKey -> webClient.post()
                .uri("/api/v1/tts")
                .header("X-API-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(byte[].class));
    }

    @Transactional
    void getSoundExistenceWord(Word word){
        byte[] englishAudio = generateSpeech(
                TtsRequest.builder()
                        .text(word.getEnglish())
                        .voice("en-US-JennyNeural")
                        .rate("-20%")
                        .pitch("+0Hz")
                        .build()
        ).block();

        attachmentRepository.findByWordIdAndLanguage(word.getId(), Language.ENGLISH)
                .ifPresent(att -> {
                    att.setData(englishAudio);
                    att.setFileType("audio/mpeg");
                    attachmentRepository.save(att);
                });

        saveMp3ToDisk(
                englishAudio,
                word.getEnglish()
        );

        sleep(31000);

        byte[] polishAudio = generateSpeech(
                TtsRequest.builder()
                        .text(word.getPolish())
                        .voice("pl-PL-ZofiaNeural")
                        .rate("-20%")
                        .pitch("+0Hz")
                        .build()
        ).block();

        attachmentRepository.findByWordIdAndLanguage(word.getId(), Language.POLISH)
                .ifPresent(att -> {
                    att.setData(polishAudio);
                    att.setFileType("audio/mpeg");
                    attachmentRepository.save(att);
                });

        saveMp3ToDisk(
                polishAudio,
                word.getPolish()
        );

        sleep(31000);

    }

    @Transactional
    void getSoundForEmptyWord(Word word){
        byte[] englishAudio = generateSpeech(
                TtsRequest.builder()
                        .text(word.getEnglish())
                        .voice("en-US-JennyNeural")
                        .rate("-20%")
                        .pitch("+0Hz")
                        .build()
        ).block();

        englishAudio = trimGeneratedAudio(englishAudio);

        Attachment englishAt = new Attachment();
        englishAt.setFileName(word.getEnglish());
        englishAt.setWordId(word.getId());
        englishAt.setFileType("audio/mpeg");
        englishAt.setLanguage(Language.ENGLISH);
        englishAt.setData(englishAudio);


//        saveMp3ToDisk(
//                englishAudio,
//                word.getEnglish()
//        );
        attachmentRepository.save(englishAt);

        sleep(31000);

        byte[] polishAudio = generateSpeech(
                TtsRequest.builder()
                        .text(word.getPolish())
                        .voice("pl-PL-ZofiaNeural")
                        .rate("-20%")
                        .pitch("+0Hz")
                        .build()
        ).block();

        polishAudio = trimGeneratedAudio(polishAudio);

        Attachment polishAt = new Attachment();
        polishAt.setFileName(word.getPolish());
        polishAt.setWordId(word.getId());
        polishAt.setFileType("audio/mpeg");
        polishAt.setLanguage(Language.POLISH);
        polishAt.setData(polishAudio);

//        saveMp3ToDisk(
//                polishAudio,
//                word.getPolish()
//        );
        attachmentRepository.save(polishAt);

        sleep(31000);

    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private byte[] trimGeneratedAudio(byte[] audio) {
        if (audio == null || audio.length == 0) {
            return audio;
        }

        return AttachmentService.trimMp3Data(audio);
    }

    private void saveMp3ToDisk(byte[] audio, String fileName) {
        try {
            Path dir = Paths.get("sounds");

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(fileName + ".mp3");

            Files.write(filePath, audio);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save mp3 file: " + fileName, e);
        }
    }
}
