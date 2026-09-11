package com.lingo_leap.service;

import com.lingo_leap.dto.AttachmentDTO;
import com.lingo_leap.enums.Language;
import com.lingo_leap.model.Attachment;
import com.lingo_leap.model.Word;
import com.lingo_leap.repository.AttachmentRepository;
import com.lingo_leap.repository.WordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final int MP3_TRIM_MILLISECONDS = 1000;
    private static final int ID3V1_TAG_SIZE = 128;
    private static final int[][] BITRATES = {
            {0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448},
            {0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384},
            {0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320},
            {0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256},
            {0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160},
            {0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160}
    };
    private static final int[][] SAMPLE_RATES = {
            {11025, 12000, 8000},
            {0, 0, 0},
            {22050, 24000, 16000},
            {44100, 48000, 32000}
    };

    private final AttachmentRepository attachmentRepository;
    private final WordRepository wordRepository;
    private final TtsService ttsService;

    @Transactional
    public Attachment saveAttachment(Long wordId, Language language, MultipartFile file) throws Exception {
        var fileName = replacePolishLetters(StringUtils.cleanPath(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence "
                        + fileName);
            }

            Optional<Attachment> attachmentOptional = attachmentRepository.findByWordIdAndLanguage(wordId, language);
            if (attachmentOptional.isPresent()) {
                Attachment attachment = attachmentOptional.get();
                attachment.setFileName(fileName);
                attachment.setData(file.getBytes());
                return attachmentRepository.save(attachment);
            } else {
                Attachment attachment = new Attachment();
                attachment.setFileName(fileName);
                attachment.setWordId(wordId);
                attachment.setFileType(file.getContentType());
                attachment.setLanguage(language);
                attachment.setData(file.getBytes());
                return attachmentRepository.save(attachment);
            }

        } catch (Exception e) {
            throw new Exception("Could not save File: " + fileName);
        }
    }

    @Transactional
    public int replacePolishLettersInAttachmentFileNames() {
        List<Attachment> changedAttachments = new ArrayList<>();

        attachmentRepository.findAll().forEach(attachment -> {
            String normalizedFileName = replacePolishLetters(attachment.getFileName());

            if (!normalizedFileName.equals(attachment.getFileName())) {
                attachment.setFileName(normalizedFileName);
                changedAttachments.add(attachment);
            }
        });

        attachmentRepository.saveAll(changedAttachments);
        return changedAttachments.size();
    }

    @Transactional
    public int trimLastSecondFromAllAttachments() throws Exception {
        List<Attachment> changedAttachments = new ArrayList<>();

        try {
            attachmentRepository.findAll().forEach(attachment -> {
                byte[] data = attachment.getData();

                if (data == null || data.length == 0) {
                    return;
                }

                byte[] trimmedData = trimLastSecondFromMp3(data);

                if (trimmedData.length != data.length) {
                    attachment.setData(trimmedData);
                    changedAttachments.add(attachment);
                }
            });
        } catch (RuntimeException e) {
            throw new Exception("Could not trim attachment audio", e);
        }

        attachmentRepository.saveAll(changedAttachments);
        return changedAttachments.size();
    }

    public Attachment getAttachment(Long fileId) throws Exception {
        return attachmentRepository
                .findById(fileId)
                .orElseThrow(
                        () -> new Exception("File not found with Id: " + fileId));
    }

    public void writeAttachmentsZip(OutputStream outputStream) throws IOException {
        Set<String> usedEntryNames = new HashSet<>();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (Attachment attachment : attachmentRepository.findAll()) {
                if (attachment.getData() == null) {
                    continue;
                }

                String entryName = createZipEntryName(attachment, usedEntryNames);
                zipOutputStream.putNextEntry(new ZipEntry(entryName));
                zipOutputStream.write(attachment.getData());
                zipOutputStream.closeEntry();
            }
        }
    }

    public Long deleteAttachmentsByWordId(Long id) {
        attachmentRepository.deleteAttachmentsByWordId(id);
        return id;
    }

    @Transactional
    public Long deleteAttachmentsById(Long id) {
        attachmentRepository.deleteById(id);
        return id;
    }

    public List<AttachmentDTO> findAll() {
        return attachmentRepository.findAllWithOutData();

    }

    public AttachmentDTO findByWordIdAndLanguageWithOutData(Long wordId, Language language) {
        return attachmentRepository.findByWordIdAndLanguageWithOutData(wordId, language);
    }

    private String createZipEntryName(Attachment attachment, Set<String> usedEntryNames) {
        String fileName = replacePolishLetters(attachment.getFileName());

        if (!StringUtils.hasText(fileName)) {
            fileName = "attachment-" + attachment.getId() + ".mp3";
        }

        fileName = Paths.get(fileName).getFileName().toString();

        if (!fileName.toLowerCase().endsWith(".mp3")) {
            fileName = fileName + ".mp3";
        }

        String entryName = fileName;

        if (usedEntryNames.contains(entryName)) {
            int extensionIndex = fileName.toLowerCase().lastIndexOf(".mp3");
            String baseName = fileName.substring(0, extensionIndex);
            entryName = baseName + "-" + attachment.getId() + ".mp3";
        }

        usedEntryNames.add(entryName);
        return entryName;
    }

    byte[] trimLastSecondFromMp3(byte[] data) {
        return trimMp3Data(data);
    }

    static byte[] trimMp3Data(byte[] data) {
        int audioStart = findAudioStart(data);
        int audioEnd = findAudioEnd(data);

        if (audioStart >= audioEnd) {
            return data;
        }

        List<Mp3Frame> frames = new ArrayList<>();
        int offset = audioStart;
        double durationMilliseconds = 0;

        while (offset + 4 <= audioEnd) {
            Mp3Frame frame = readMp3Frame(data, offset, audioEnd);

            if (frame == null) {
                offset++;
                continue;
            }

            frames.add(frame);
            durationMilliseconds += frame.durationMilliseconds();
            offset += frame.size();
        }

        if (frames.isEmpty() || durationMilliseconds <= MP3_TRIM_MILLISECONDS) {
            return data;
        }

        double keepMilliseconds = durationMilliseconds - MP3_TRIM_MILLISECONDS;
        double keptMilliseconds = 0;
        int trimOffset = frames.get(frames.size() - 1).offset();

        for (Mp3Frame frame : frames) {
            if (keptMilliseconds + frame.durationMilliseconds() > keepMilliseconds) {
                trimOffset = frame.offset();
                break;
            }

            keptMilliseconds += frame.durationMilliseconds();
        }

        if (trimOffset <= audioStart) {
            return data;
        }

        byte[] trimmedData = new byte[trimOffset + data.length - audioEnd];
        System.arraycopy(data, 0, trimmedData, 0, trimOffset);
        System.arraycopy(data, audioEnd, trimmedData, trimOffset, data.length - audioEnd);
        return trimmedData;
    }

    private static int findAudioStart(byte[] data) {
        if (data.length < 10 || data[0] != 'I' || data[1] != 'D' || data[2] != '3') {
            return 0;
        }

        int tagSize = ((data[6] & 0x7F) << 21)
                | ((data[7] & 0x7F) << 14)
                | ((data[8] & 0x7F) << 7)
                | (data[9] & 0x7F);
        return Math.min(data.length, 10 + tagSize);
    }

    private static int findAudioEnd(byte[] data) {
        if (data.length >= ID3V1_TAG_SIZE
                && data[data.length - ID3V1_TAG_SIZE] == 'T'
                && data[data.length - ID3V1_TAG_SIZE + 1] == 'A'
                && data[data.length - ID3V1_TAG_SIZE + 2] == 'G') {
            return data.length - ID3V1_TAG_SIZE;
        }

        return data.length;
    }

    private static Mp3Frame readMp3Frame(byte[] data, int offset, int audioEnd) {
        if ((data[offset] & 0xFF) != 0xFF || (data[offset + 1] & 0xE0) != 0xE0) {
            return null;
        }

        int version = (data[offset + 1] >> 3) & 0x03;
        int layer = (data[offset + 1] >> 1) & 0x03;
        int bitrateIndex = (data[offset + 2] >> 4) & 0x0F;
        int sampleRateIndex = (data[offset + 2] >> 2) & 0x03;
        int padding = (data[offset + 2] >> 1) & 0x01;

        if (version == 1 || layer == 0 || bitrateIndex == 0 || bitrateIndex == 15 || sampleRateIndex == 3) {
            return null;
        }

        int sampleRate = SAMPLE_RATES[version][sampleRateIndex];
        int bitrate = BITRATES[bitrateTableIndex(version, layer)][bitrateIndex] * 1000;
        int samplesPerFrame = samplesPerFrame(version, layer);
        int frameSize = frameSize(version, layer, bitrate, sampleRate, padding);

        if (sampleRate == 0 || frameSize <= 0 || offset + frameSize > audioEnd) {
            return null;
        }

        return new Mp3Frame(offset, frameSize, samplesPerFrame * 1000.0 / sampleRate);
    }

    private static int bitrateTableIndex(int version, int layer) {
        if (version == 3) {
            return 3 - layer;
        }

        return 6 - layer;
    }

    private static int samplesPerFrame(int version, int layer) {
        if (layer == 3) {
            return 384;
        }

        if (layer == 2 || version == 3) {
            return 1152;
        }

        return 576;
    }

    private static int frameSize(int version, int layer, int bitrate, int sampleRate, int padding) {
        if (layer == 3) {
            return (12 * bitrate / sampleRate + padding) * 4;
        }

        if (layer == 1 && version != 3) {
            return 72 * bitrate / sampleRate + padding;
        }

        return 144 * bitrate / sampleRate + padding;
    }

    String replacePolishLetters(String value) {
        if (value == null) {
            return null;
        }

        String replaced = value
                .replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z")
                .replace("Ą", "A")
                .replace("Ć", "C")
                .replace("Ę", "E")
                .replace("Ł", "L")
                .replace("Ń", "N")
                .replace("Ó", "O")
                .replace("Ś", "S")
                .replace("Ź", "Z")
                .replace("Ż", "Z");

        return Normalizer.normalize(replaced, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private record Mp3Frame(int offset, int size, double durationMilliseconds) {
    }

    public Boolean getAttachmentsForEmptyWords() {
        List<Word> words = wordRepository.findWordsWithOutAttachments();

        for (Word word : words) {
            ttsService.getSoundForEmptyWord(word);
        }

        replacePolishLettersInAttachmentFileNames();
        return true;
    }
}
