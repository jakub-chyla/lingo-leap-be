package com.lingo_leap.controller;

import com.lingo_leap.dto.Answer;
import com.lingo_leap.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<Integer> saveAnswer(@RequestBody Answer answer){
        return ResponseEntity.ok(historyService.saveAnswer(answer));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getIncorrectCount(@PathVariable Long userId){
        return ResponseEntity.ok(historyService.findCountOfIncorrect(userId));
    }
}
