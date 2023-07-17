package com.example.wonderwoman.auth.service;

import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public void joinMember(MemberRequestDto requestDto) {
        if (findMemberByEmail(requestDto.getEmail()))
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");

        Member member = requestDto.toMember(passwordEncoder);
        memberRepository.save(member);
    }

    public void changeTempPw(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다"));
        member.updatePassword(passwordEncoder.encode((newPassword)));
    }

    /*
    //로그인
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
    }

    // Access Token가 만료일자만 초과한 토큰인지 검사
    public boolean validate(String requestAccessTokenInHeader) {
    }

    // 토큰 재발급: validate가 true일 때 access, refresh 모두 재발급
    @Transactional
    public TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
    }

    // 토큰 발급
    @Transactional
    public TokenDto generateToken(String provider, String id, String authorities) {

    }

    // RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
    }

    // AT로부터 principal 추출
    public String getPrincipal(String requestAccessToken) {
    }

    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessTokenInHeader) {
    }

    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
    }
    */

    public boolean findMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean findMemberByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
