package com.example.wonderwoman.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NicknameRequestDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;
}
