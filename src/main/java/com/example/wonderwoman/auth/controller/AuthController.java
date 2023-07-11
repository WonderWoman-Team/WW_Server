package com.example.wonderwoman.auth.controller;

import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.auth.request.NicknameRequestDto;
import com.example.wonderwoman.auth.service.AuthService;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.mail.request.EmailRequestDto;
import com.example.wonderwoman.mail.response.EmailConfirmResponseDto;
import com.example.wonderwoman.mail.service.ChangePwEmailService;
import com.example.wonderwoman.mail.service.SignupEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final SignupEmailService signupEmailService;

    private final ChangePwEmailService changePwEmailService;

    @PostMapping("/signup")
    public ResponseEntity<NormalResponseDto> join(@RequestBody @Valid MemberRequestDto requestDto) {
        authService.joinMember(requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    //이메일 중복 체크
    @PostMapping("/mail-check")
    public ResponseEntity<NormalResponseDto> checkDuplicateEmail(@RequestBody @Valid EmailRequestDto requestDto) {
        if (isAlreadyExistEmail(requestDto.getEmail()))
            return ResponseEntity.ok(NormalResponseDto.fail());
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    //닉네임 중복 체크
    @PostMapping("/nickname-check")
    public ResponseEntity<NormalResponseDto> checkDuplicateNickname(@RequestBody @Valid NicknameRequestDto requestDto) {
        if (isAlreadyExistNickname(requestDto.getNickname()))
            return ResponseEntity.ok(NormalResponseDto.fail());
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    @PostMapping("/signup/mailConfirm")
    @ResponseBody
    public ResponseEntity<EmailConfirmResponseDto> mailConfirm(@RequestBody EmailRequestDto requestDto) throws Exception {
        String code = signupEmailService.sendSimpleMessage(requestDto.getEmail());
        return ResponseEntity.ok(new EmailConfirmResponseDto(code));
    }

    @PostMapping("/findPw")
    @ResponseBody
    public ResponseEntity<NormalResponseDto> findPw(@RequestBody EmailRequestDto requestDto) throws Exception {
        if (!isAlreadyExistEmail(requestDto.getEmail()))
            return ResponseEntity.ok(NormalResponseDto.fail());
        String newPassword = changePwEmailService.sendSimpleMessage(requestDto.getEmail());
        authService.changeTempPw(requestDto.getEmail(), newPassword);
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    /*
    //로그인 api -> 인증 정보 저장 및 토큰 발급
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {

    }

    //토큰 재발급 여부 확인
    @PostMapping("/validate")
    public ResponseEntity<NormalResponse> validate(@RequestHeader("Authorization") String requestAccessToken) {
    }

    //토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<NormalResponse> logout(@RequestHeader("Authorization") String requestAccessToken) {
    }

     */

    public boolean isAlreadyExistEmail(String email) {
        return authService.findMemberByEmail(email);
    }

    private boolean isAlreadyExistNickname(String nickname) {
        return authService.findMemberByNickname(nickname);
    }
}
