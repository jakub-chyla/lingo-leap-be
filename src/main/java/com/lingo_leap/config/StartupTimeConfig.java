package com.lingo_leap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class StartupTimeConfig {

    @Bean
    public LocalDateTime startupTime() {
        return LocalDateTime.now();
    }
}
