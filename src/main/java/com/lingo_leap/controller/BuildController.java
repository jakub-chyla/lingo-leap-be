package com.lingo_leap.controller;

import com.lingo_leap.service.StartUpTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {

    private final StartUpTimeService startUpTimeService;


    @GetMapping
    LocalDateTime getLastBuildTime(){
        return startUpTimeService.getStartupTime();
    }
}
