package com.example.wonderwoman.util;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.time.Duration;

public class CookieUtils {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static String requestAccessToken;

    public static void setAccessTokenCookie(ServerHttpResponse response, String token, Duration duration) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .maxAge(duration)
                .path("/")
                .build();
        response.addCookie(cookie);
    }

    public static void setRefreshTokenCookie(ServerHttpResponse response, String token, Duration duration) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .maxAge(duration)
                .path("/")
                .build();
        response.addCookie(cookie);
    }

    public static String getAccessTokenFromRequest(ServerHttpRequest request) {
        return request.getCookies().getFirst(ACCESS_TOKEN_COOKIE_NAME).getValue();
    }

    public static String getRefreshTokenFromRequest(ServerHttpRequest request) {
        return request.getCookies().getFirst(REFRESH_TOKEN_COOKIE_NAME).getValue();
    }

    public static String resolveToken(String requestAccessToken) {
        CookieUtils.requestAccessToken = requestAccessToken;
        return requestAccessToken;
    }
}
