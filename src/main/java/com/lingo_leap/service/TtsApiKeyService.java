package com.lingo_leap.service;

import com.lingo_leap.model.TtsApiKey;
import com.lingo_leap.repository.TtsApiKeyRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class TtsApiKeyService {
    private final TtsApiKeyRepository repository;

    public TtsApiKeyService(TtsApiKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String claimKey() {
        LocalDate today = LocalDate.now();
        Instant now = Instant.now();
        TtsApiKey key = repository.findAvailableForUpdate(today, now.minusSeconds(120), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No TTS API key is available (keys may be cooling down or at today's limit)"));

        if (!today.equals(key.getUsageDate())) {
            key.setUsageDate(today);
            key.setDailyUsage(0);
        }
        key.setDailyUsage(key.getDailyUsage() + 1);
        key.setLastUsedAt(now);
        repository.save(key);
        return key.getApiKey();
    }
}
