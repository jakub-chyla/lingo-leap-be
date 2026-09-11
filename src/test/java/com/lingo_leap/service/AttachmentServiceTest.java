package com.lingo_leap.service;

import com.lingo_leap.model.Attachment;
import com.lingo_leap.repository.AttachmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void replacePolishLettersReplacesAllPolishCharacters() {
        assertEquals(
                "acelnoszzACELNOSZZ.mp3",
                attachmentService.replacePolishLetters("ąćęłńóśźżĄĆĘŁŃÓŚŹŻ.mp3")
        );
    }

    @Test
    void replacePolishLettersInAttachmentFileNamesUpdatesOnlyChangedAttachments() {
        Attachment polishAttachment = new Attachment();
        polishAttachment.setId(1L);
        polishAttachment.setFileName("zażółć gęślą jaźń.mp3");

        Attachment normalizedAttachment = new Attachment();
        normalizedAttachment.setId(2L);
        normalizedAttachment.setFileName("hello.mp3");

        when(attachmentRepository.findAll()).thenReturn(List.of(polishAttachment, normalizedAttachment));

        int updatedCount = attachmentService.replacePolishLettersInAttachmentFileNames();

        ArgumentCaptor<List<Attachment>> captor = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepository).saveAll(captor.capture());

        assertEquals(1, updatedCount);
        assertEquals(1, captor.getValue().size());
        assertEquals("zazolc gesla jazn.mp3", captor.getValue().get(0).getFileName());
    }

    @Test
    void writeAttachmentsZipWritesMp3Files() throws Exception {
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test.mp3");
        attachment.setData(new byte[]{1, 2, 3});

        when(attachmentRepository.findAll()).thenReturn(List.of(attachment));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        attachmentService.writeAttachmentsZip(outputStream);

        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            assertEquals("test.mp3", entry.getName());
            assertTrue(Arrays.equals(new byte[]{1, 2, 3}, zipInputStream.readAllBytes()));
        }
    }

    @Test
    void trimLastSecondFromAllAttachmentsCutsMp3DataAndSavesChangedAttachments() throws Exception {
        byte[] mp3Data = createMpeg1Layer3Frames(80);

        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test.mp3");
        attachment.setData(mp3Data);

        Attachment emptyAttachment = new Attachment();
        emptyAttachment.setId(2L);
        emptyAttachment.setFileName("empty.mp3");

        when(attachmentRepository.findAll()).thenReturn(List.of(attachment, emptyAttachment));

        int updatedCount = attachmentService.trimLastSecondFromAllAttachments();

        ArgumentCaptor<List<Attachment>> captor = ArgumentCaptor.forClass(List.class);
        verify(attachmentRepository).saveAll(captor.capture());

        assertEquals(1, updatedCount);
        assertEquals(1, captor.getValue().size());
        assertEquals(41 * 417, captor.getValue().get(0).getData().length);
    }

    private byte[] createMpeg1Layer3Frames(int frameCount) {
        int frameSize = 417;
        byte[] data = new byte[frameCount * frameSize];

        for (int frame = 0; frame < frameCount; frame++) {
            int offset = frame * frameSize;
            data[offset] = (byte) 0xFF;
            data[offset + 1] = (byte) 0xFB;
            data[offset + 2] = (byte) 0x90;
            data[offset + 3] = 0;
        }

        return data;
    }
}
