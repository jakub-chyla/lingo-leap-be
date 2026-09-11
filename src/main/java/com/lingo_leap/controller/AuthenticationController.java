package com.lingo_leap.controller;

import com.lingo_leap.dto.AuthRequest;
import com.lingo_leap.dto.AuthenticationResponse;
import com.lingo_leap.dto.AuthenticationResponseDto;
import com.lingo_leap.model.User;
import com.lingo_leap.service.AuthenticationService;
import com.lingo_leap.service.EmailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final AuthenticationService authService;

    private final EmailSenderService emailSenderService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthRequest request) {
        //TODO: move to service
        emailSenderService.sendEmail(request.getEmail(),
                "Account created",
                "Hi, " + request.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody User request) {
        AuthenticationResponseDto authResponse = authService.authenticate(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }
}
