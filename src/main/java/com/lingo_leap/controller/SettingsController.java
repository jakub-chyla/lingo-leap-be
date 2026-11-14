package com.lingo_leap.controller;

import com.lingo_leap.dto.SettingsRequest;
import com.lingo_leap.model.Settings;
import com.lingo_leap.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<Settings> getSettings() {
        return ResponseEntity.ok(settingsService.getSettingsForLoggedInUser());
    }

    @PutMapping
    public ResponseEntity<Settings> updateSettings(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettingsForLoggedInUser(request));
    }
}
