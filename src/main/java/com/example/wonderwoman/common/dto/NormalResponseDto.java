package com.example.wonderwoman.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NormalResponseDto {
    private String status;
    private String message;

    
    protected NormalResponseDto(String status) {
        this.status = status;
    }

    public static NormalResponseDto success() {
        return new NormalResponseDto("SUCCESS");
    }

    public static NormalResponseDto fail() {
        return new NormalResponseDto("FAIL");
    }

    public static NormalResponseDto error() { return new NormalResponseDto("올바른 건물을 선택해주세요."); }

    public void setMessage(String message) {
        this.message = message;
    }
}
