package com.example.wonderwoman.auth.controller;

import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.auth.request.NicknameRequestDto;
import com.example.wonderwoman.auth.service.AuthService;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.login.request.LoginRequestDto;
import com.example.wonderwoman.mail.request.EmailRequestDto;
import com.example.wonderwoman.mail.service.ChangePwEmailService;
import com.example.wonderwoman.mail.service.SignupEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final long COOKIE_EXPIRATION = 7776000; // 90일

    private final AuthService authService;

    private final SignupEmailService signupEmailService;

    private final PasswordEncoder passwordEncoder;

    private final ChangePwEmailService changePwEmailService;

    @PostMapping("/signup")
    public ResponseEntity<NormalResponseDto> join(@RequestBody @Valid MemberRequestDto requestDto) {
        authService.joinMember(requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    //닉네임 중복 체크
    @PostMapping("/signup/nickname-check")
    public ResponseEntity<NormalResponseDto> checkDuplicateNickname(@RequestBody @Valid NicknameRequestDto requestDto) {
        if (isAlreadyExistNickname(requestDto.getNickname()))
            return ResponseEntity.ok(NormalResponseDto.fail());
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    @PostMapping("/signup/mailConfirm")
    @ResponseBody
    public ResponseEntity<NormalResponseDto> mailConfirm(@RequestBody EmailRequestDto requestDto) throws Exception {
        if (isAlreadyExistEmail(requestDto.getEmail()))
            return ResponseEntity.ok(NormalResponseDto.fail());

        String code = signupEmailService.sendSimpleMessage(requestDto.getEmail());
        NormalResponseDto responseDto = NormalResponseDto.success();
        responseDto.setMessage(code);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/signup/findPw")
    @ResponseBody
    public ResponseEntity<NormalResponseDto> findPw(@RequestBody EmailRequestDto requestDto) throws Exception {
        if (!isAlreadyExistEmail(requestDto.getEmail()))
            return ResponseEntity.ok(NormalResponseDto.fail());
        String newPassword = changePwEmailService.sendSimpleMessage(requestDto.getEmail());
        authService.changeTempPw(requestDto.getEmail(), newPassword);
        return ResponseEntity.ok(NormalResponseDto.success());
    }


    //로그인 api -> 인증 정보 저장 및 토큰 발급

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        TokenDto tokenDto = authService.login(loginRequest);

        HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(true)
                .sameSite(Cookie.SameSite.NONE.attributeValue())    //서드파티 쿠키 사용 허용
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }


    //토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {

        TokenDto newAuthToken = authService.reissue(requestAccessToken, requestRefreshToken);

        if (newAuthToken != null) {
            // 새로운 토큰 발급, 반환
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", newAuthToken.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite(Cookie.SameSite.NONE.attributeValue())
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAuthToken.getAccessToken())
                    .build();
        } else {
            //  Refresh Token이 탈취 가능할 때 쿠키 삭제하고 재로그인
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<NormalResponseDto> logout(@RequestHeader("Authorization") String requestAccessToken) {
        // Access Token을 무효화하여 로그아웃 처리
        authService.logout(requestAccessToken);

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();


        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }


    public boolean isAlreadyExistEmail(String email) {
        return authService.findMemberByEmail(email);
    }

    private boolean isAlreadyExistNickname(String nickname) {
        return authService.findMemberByNickname(nickname);
    }

}