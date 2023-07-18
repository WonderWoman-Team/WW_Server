package com.example.wonderwoman.auth.controller;

import com.example.wonderwoman.auth.Token.AuthToken;
import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.auth.request.NicknameRequestDto;
import com.example.wonderwoman.auth.service.AuthService;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.jwt.JwtTokenProvider;
import com.example.wonderwoman.login.LoginRequestDto;
import com.example.wonderwoman.login.LoginResponseDto;
import com.example.wonderwoman.mail.request.EmailRequestDto;
import com.example.wonderwoman.mail.response.EmailConfirmResponseDto;
import com.example.wonderwoman.mail.service.ChangePwEmailService;
import com.example.wonderwoman.mail.service.SignupEmailService;
import com.example.wonderwoman.member.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.example.wonderwoman.jwt.JwtTokenProvider.createToken;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final SignupEmailService signupEmailService;

    private final ChangePwEmailService changePwEmailService;
    private final PasswordEncoder passwordEncoder;

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


    //로그인 api -> 인증 정보 저장 및 토큰 발급

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // 입력받은 이메일로 사용자를 조회합니다.
            Member member = authService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 이메일입니다."));

            // 입력받은 비밀번호와 DB에 저장된 비밀번호를 비교하여 인증합니다.
            if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }

            // 인증에 성공하면 AccessToken과 RefreshToken을 발급합니다.
            TokenDto authToken = createToken(member.getId().toString(), member.getAuthorities());

            // AuthToken을 TokenDto로 변환하여 반환합니다.
            TokenDto tokenDto = new TokenDto(authToken.getAccessToken(), authToken.getRefreshToken());

            // AccessToken과 RefreshToken을 LoginResponseDto에 담아서 반환합니다.
            return ResponseEntity.ok(new LoginResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken()));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            // 로그인 실패 시 예외 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDto(null, null));
        }
    }



    //토큰 재발급 여부 확인
    @PostMapping("/validate")
    public ResponseEntity<NormalResponseDto> validate(@RequestHeader("Authorization") String requestAccessToken) {
        try {
            // Access Token의 유효성 검사
            boolean isValidAccessToken = authService.validate(requestAccessToken);

            if (isValidAccessToken) {
                // Access Token이 유효한 경우
                return ResponseEntity.ok(NormalResponseDto.success());
            } else {
                // Access Token이 유효하지 않은 경우
                // Refresh Token에서 유효성 검사를 시도하여 확인
                String requestRefreshToken = extractRefreshTokenFromHeader(requestAccessToken);
                boolean isValidRefreshToken = authService.validateRefreshToken(requestRefreshToken);

                if (isValidRefreshToken) {
                    // Refresh Token이 유효한 경우
                    return ResponseEntity.ok(NormalResponseDto.success());
                } else {
                    // Refresh Token이 유효하지 않은 경우
                    return ResponseEntity.ok(NormalResponseDto.fail());
                }
            }
        } catch (Exception e) {
            // 토큰 검사 중에 예외 발생 시 유효하지 않은 토큰으로 처리
            return ResponseEntity.ok(NormalResponseDto.fail());
        }
    }


    //토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {

        try {
            // Access Token과 Refresh Token의 유효성 검사
            boolean isValidAccessToken = authService.validate(requestAccessToken);
            boolean isValidRefreshToken = authService.validateRefreshToken(requestRefreshToken);

            if (isValidAccessToken && isValidRefreshToken) {
                // Access Token에서 유저 정보 추출
                Authentication authentication = JwtTokenProvider.getAuthentication(requestAccessToken);
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String userId = userDetails.getUsername();

                // 새로운 Access Token과 Refresh Token 발급
                TokenDto newAuthToken = authService.reissue(requestAccessToken, requestRefreshToken);

                if (newAuthToken != null) {
                    // 새로운 TokenDto 반환
                    return ResponseEntity.ok(newAuthToken);
                } else {
                    // 유효하지 않은 토큰이거나 Refresh Token이 유효하지 않은 경우
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                // 유효하지 않은 토큰이거나 Refresh Token이 유효하지 않은 경우
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            // 토큰 검사 중에 예외 발생 시 유효하지 않은 토큰으로 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<NormalResponseDto> logout(@RequestHeader("Authorization") String requestAccessToken) {
        try {
            // Access Token을 무효화하여 로그아웃 처리
            authService.logout(requestAccessToken);

            // 응답으로 성공 상태와 메시지를 전달
            NormalResponseDto responseDto = NormalResponseDto.success();
            responseDto.setMessage("Logged out successfully.");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // 로그아웃 실패 시 예외 처리
            NormalResponseDto responseDto = NormalResponseDto.fail();
            responseDto.setMessage("Logout failed.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }


    public boolean isAlreadyExistEmail(String email) {
        return authService.findMemberByEmail(email);
    }

    private boolean isAlreadyExistNickname(String nickname) {
        return authService.findMemberByNickname(nickname);
    }

    //extractRefreshTokenFromHeader 메서드 구현
    // "Bearer {AT}"에서 {AT} 추출
    private String extractRefreshTokenFromHeader(String requestAccessTokenInHeader) {
        String bearerPrefix = "Bearer ";
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith(bearerPrefix)) {
            return requestAccessTokenInHeader.substring(bearerPrefix.length());
        }
        // "Bearer "로 시작하지 않거나, 토큰 부분이 없는 경우 null을 반환합니다.
        return null;
    }

}