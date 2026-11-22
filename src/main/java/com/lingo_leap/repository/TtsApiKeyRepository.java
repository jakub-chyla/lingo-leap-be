package com.lingo_leap.repository;

import com.lingo_leap.model.TtsApiKey;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface TtsApiKeyRepository extends JpaRepository<TtsApiKey, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT k FROM TtsApiKey k
            WHERE (k.usageDate < :today OR k.dailyUsage < 10)
              AND (k.lastUsedAt IS NULL OR k.lastUsedAt <= :cooldownCutoff)
            ORDER BY CASE WHEN k.usageDate < :today THEN 0 ELSE k.dailyUsage END,
                     CASE WHEN k.lastUsedAt IS NULL THEN 0 ELSE 1 END,
                     k.lastUsedAt,
                     k.id
            """)
    List<TtsApiKey> findAvailableForUpdate(@Param("today") LocalDate today,
                                           @Param("cooldownCutoff") Instant cooldownCutoff,
                                           org.springframework.data.domain.Pageable pageable);
}
