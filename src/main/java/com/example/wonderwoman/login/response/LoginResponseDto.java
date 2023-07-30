package com.example.wonderwoman.login.response;

public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;

    // 생성자
    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getter 메서드
    public String getAccessToken() {
        return accessToken;
    }

    // Setter 메서드
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
