package com.lingo_leap.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {


    @GetMapping()
    ResponseEntity getHealth(){
        return ResponseEntity.ok("ok");
    }
}
