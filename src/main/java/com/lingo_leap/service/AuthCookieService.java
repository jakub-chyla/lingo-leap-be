package com.lingo_leap.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class AuthCookieService {
    public static final String ACCESS_COOKIE = "access_token";
    public static final String REFRESH_COOKIE = "refresh_token";

    @Value("${application.security.cookie.secure:false}")
    private boolean secure;
    @Value("${application.security.cookie.same-site:Lax}")
    private String sameSite;
    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void write(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(ACCESS_COOKIE, accessToken, accessTokenExpiration).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, refreshToken, refreshTokenExpiration).toString());
    }

    public void clear(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(ACCESS_COOKIE, "", 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(REFRESH_COOKIE, "", 0).toString());
    }

    public String read(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) if (name.equals(cookie.getName())) return cookie.getValue();
        return null;
    }

    private ResponseCookie cookie(String name, String value, long maxAgeMillis) {
        return ResponseCookie.from(name, value).httpOnly(true).secure(secure)
                .sameSite(sameSite).path("/").maxAge(Duration.ofMillis(maxAgeMillis)).build();
    }
}
