package com.lingo_leap.controller;

import com.lingo_leap.record.UserReinforcement;
import com.lingo_leap.service.ReinforcementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reinforcement")
public class ReinforcementController {

    private final ReinforcementService reinforcementService;

    @PostMapping()
    public ResponseEntity<Boolean> setReinforcement(@RequestBody UserReinforcement user) {
        return ResponseEntity.ok(reinforcementService.setReinforcement(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Boolean> getReinforcement(@PathVariable Long userId) {
        return ResponseEntity.ok(reinforcementService.getReinforcement(userId));
    }
}
