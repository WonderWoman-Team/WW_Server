package com.example.wonderwoman.login;

public class LoginRequestDto {
    private String email;
    private String password;

    // 생성자
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter 메서드
    public String getEmail() {
        return email;
    }

    // Setter 메서드
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
