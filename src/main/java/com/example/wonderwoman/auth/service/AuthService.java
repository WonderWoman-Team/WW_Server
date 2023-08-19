package com.example.wonderwoman.auth.service;

import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.login.request.LoginRequestDto;
import com.example.wonderwoman.login.service.JwtTokenProvider;
import com.example.wonderwoman.login.service.RedisService;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    private final String SERVER = "Server";

    //회원가입
    @Transactional
    public void joinMember(MemberRequestDto requestDto) {
        if (findMemberByEmail(requestDto.getEmail()))
            throw new WonderException(ErrorCode.ALREADY_MEMBER);

        Member member = requestDto.toMember(passwordEncoder); // 비밀번호를 인코딩하여 저장
        memberRepository.save(member);
    }

    //임시 비밀번호로 변경(사용자가 x, 시스템에서)
    @Transactional
    public void changeTempPw(Member member, String newPassword) {
        member.updatePassword(passwordEncoder.encode(newPassword)); // 변경된 비밀번호를 인코딩하여 저장
        memberRepository.save(member);
    }

    //로그인
    @Transactional
    public TokenDto login(LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        Authentication authentication = authenticationManager.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateToken(SERVER, authentication.getName(), getAuthorities(authentication));
    }


    // Access Token가 만료만 된 (유효한) 토큰인지 검사
    public boolean validate(String requestAccessTokenInHeader) {
        String requestRefreshToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateTokenOnlyExpired(requestRefreshToken);
    }


    // 토큰 재발급: validate가 true일 때 access, refresh 모두 재발급
    @Transactional
    public TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String accessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        String principal = getPrincipal(accessToken);

        String redisRefreshToken = redisService.getValues("refresh-token:" + SERVER + ":" + principal);

        //저장된 refresh 없으면 재로그인 요청
        if (redisRefreshToken == null) {
            return null;
        }

        //refresh가 redis와 다르거나 유효하지 않으면 삭제하고 재로그인 요청
        if (!jwtTokenProvider.validateRefreshToken(requestRefreshToken) ||
                !redisRefreshToken.equals(requestRefreshToken)) {
            redisService.deleteValues("refresh-token:" + SERVER + ":" + principal);
            return null;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        //기존 refresh 삭제하고 토큰 재발급 및 저장
        redisService.deleteValues("refresh-token:" + SERVER + ":" + principal);
        TokenDto tokenDto = jwtTokenProvider.createToken(principal, authorities);
        saveRefreshToken(SERVER, principal, tokenDto.getRefreshToken());
        return tokenDto;

    }

    // 토큰 발급
    @Transactional
    public TokenDto generateToken(String provider, String email, String authorities) {
        //refresh 이미 있을 경우 삭제
        if (redisService.getValues("refresh-token:" + provider + ":" + email) != null) {
            redisService.deleteValues("refresh-token:" + provider + ":" + email);
        }

        // 토큰 재발급 후 저장
        TokenDto authToken = jwtTokenProvider.createToken(email, authorities);
        saveRefreshToken(provider, email, authToken.getRefreshToken());

        // 발급된 TokenDto 반환
        return authToken;
    }


    // RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        // 저장할 Redis 키를 생성합니다. 여기서는 "refresh_token:provider:principal" 형태로 지정합니다.
        String redisKey = "refresh-token:" + provider + ":" + principal;

        // refreshToken의 만료 시간을 설정해주는 것이 일반적이며, 실제 사용 시에는 해당 기능도 추가해야합니다.
        redisService.setValuesWithTimeout(redisKey,
                refreshToken,
                jwtTokenProvider.getTokenExpirationTime(refreshToken));
        // 만료 시간을 설정하지 않으면 refreshToken은 Redis에 영구히 저장됩니다.
        // 예를 들어, redisService.set(redisKey, refreshToken, 만료시간) 과 같이 만료 시간을 설정합니다.
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        // 권한 이름들을 ","로 구분하여 하나의 문자열로 변환합니다.
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }


    // AT로부터 principal 추출
    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }


    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }


    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        String principal = getPrincipal(requestAccessToken);

        // (선택사항) Redis 또는 다른 저장소에서 관련 정보 삭제
        String refreshToken = redisService.getValues("refresh-token:" + SERVER + ":" + principal);
        if (refreshToken != null) {
            redisService.deleteValues("refresh-token:" + SERVER + ":" + principal);
        }

        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken);
        redisService.setValuesWithTimeout(requestAccessToken,
                "logout",
                expiration);
    }

    ////////////////

    public boolean findMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean findMemberByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

}