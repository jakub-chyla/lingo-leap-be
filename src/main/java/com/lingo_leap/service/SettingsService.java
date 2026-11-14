package com.lingo_leap.service;

import com.lingo_leap.dto.SettingsRequest;
import com.lingo_leap.model.Settings;
import com.lingo_leap.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final UserDetailsServiceImp userDetailsService;

    @Transactional
    public Settings getSettingsForLoggedInUser() {
        Long userId = userDetailsService.getLoggedInUser().getId();
        return getSettingsForUser(userId);
    }

    @Transactional
    public Settings getSettingsForUser(Long userId) {
        return settingsRepository.findById(userId).orElseGet(() -> createDefaultSettings(userId));
    }

    @Transactional
    public Settings updateSettingsForLoggedInUser(SettingsRequest request) {
        if (request == null || request.mostWrong() == null || request.leastAnswered() == null) {
            throw new IllegalArgumentException("Both mostWrong and leastAnswered values are required");
        }

        Long userId = userDetailsService.getLoggedInUser().getId();
        Settings settings = settingsRepository.findById(userId).orElseGet(() -> createDefaultSettings(userId));
        settings.setMostWrong(request.mostWrong());
        settings.setLeastAnswered(request.leastAnswered());
        return settingsRepository.save(settings);
    }

    private Settings createDefaultSettings(Long userId) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        return settingsRepository.save(settings);
    }
}
