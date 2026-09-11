package com.lingo_leap.scheduler;

import com.lingo_leap.service.ReinforcementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class Scheduler {

    private final ReinforcementService reinforcementService;
    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    public void removeReinforcements() {
        reinforcementService.resetAll();
    }
}
