package com.lingo_leap.service;

import com.lingo_leap.dto.AuthRequest;
import com.lingo_leap.dto.AuthenticationResponse;
import com.lingo_leap.dto.AuthenticationResponseDto;
import com.lingo_leap.enums.Role;
import com.lingo_leap.model.Token;
import com.lingo_leap.model.User;
import com.lingo_leap.repository.TokenRepository;
import com.lingo_leap.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthCookieService authCookieService;

    public AuthenticationResponse register(AuthRequest request, HttpServletResponse response) {

        if (repository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse("User already exist");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user = repository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(accessToken, refreshToken, user);
        authCookieService.write(response, accessToken, refreshToken);

        return new AuthenticationResponse("User registration was successful");
    }

    public AuthenticationResponseDto authenticate(User request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(accessToken, refreshToken, user);
        authCookieService.write(response, accessToken, refreshToken);
        AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();
        authenticationResponseDto.setId(user.getId());
        authenticationResponseDto.setUserName(user.getUsername());
        authenticationResponseDto.setRole(user.getRole());
        authenticationResponseDto.setMessage("User login was successful");

        return authenticationResponseDto;
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    public ResponseEntity refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        String token = authCookieService.read(request.getCookies(), AuthCookieService.REFRESH_COOKIE);
        if (token == null || token.isBlank()) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(token);

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No user found"));

        if (jwtService.isValidRefreshToken(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);
            authCookieService.write(response, accessToken, refreshToken);

            return new ResponseEntity(new AuthenticationResponseDto(user.getId(), user.getUsername(), user.getRole(), "New token generated"), HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    private Token saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        return tokenRepository.save(token);
    }
}
