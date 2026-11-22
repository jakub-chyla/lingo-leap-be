package com.lingo_leap.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "tts_api_keys")
public class TtsApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "daily_usage", nullable = false)
    private int dailyUsage;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

}
