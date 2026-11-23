package com.lingo_leap.config;

import com.lingo_leap.model.Token;
import com.lingo_leap.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import com.lingo_leap.service.AuthCookieService;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final AuthCookieService authCookieService;

    public CustomLogoutHandler(TokenRepository tokenRepository, AuthCookieService authCookieService) {
        this.tokenRepository = tokenRepository;
        this.authCookieService = authCookieService;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authCookieService.read(request.getCookies(), AuthCookieService.ACCESS_COOKIE);
        authCookieService.clear(response);
        if (token == null) return;
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);

        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }
}
