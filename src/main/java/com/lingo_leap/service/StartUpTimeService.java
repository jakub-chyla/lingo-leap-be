package com.lingo_leap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StartUpTimeService {

    private final LocalDateTime startupTime;

    public LocalDateTime getStartupTime() {
        return startupTime;
    }
}