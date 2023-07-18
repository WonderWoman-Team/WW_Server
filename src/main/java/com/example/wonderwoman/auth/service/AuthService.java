package com.example.wonderwoman.auth.service;

import com.example.wonderwoman.auth.Token.AuthToken;
import com.example.wonderwoman.auth.request.MemberRequestDto;
import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.jwt.JwtTokenProvider;
import com.example.wonderwoman.login.LoginRequestDto;
import com.example.wonderwoman.login.LoginResponseDto;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.wonderwoman.jwt.JwtTokenProvider.redisService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    // 회원가입
    public void joinMember(MemberRequestDto requestDto) {
        if (findMemberByEmail(requestDto.getEmail()))
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");

        Member member = requestDto.toMember(passwordEncoder); // 비밀번호를 인코딩하여 저장
        memberRepository.save(member);
    }

    public void changeTempPw(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다"));
        member.updatePassword(passwordEncoder.encode(newPassword)); // 변경된 비밀번호를 인코딩하여 저장
    }

    /////////////////////////////////////////
    //로그인
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 입력받은 이메일로 사용자를 조회합니다.
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 입력받은 비밀번호와 DB에 저장된 비밀번호를 비교하여 인증합니다.
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 인증에 성공하면 AccessToken과 RefreshToken을 발급합니다.
        TokenDto authToken = JwtTokenProvider.createToken(member.getId().toString(), member.getAuthorities());

        // AuthToken을 TokenDto로 변환하여 반환합니다.
        TokenDto tokenDto = new TokenDto(authToken.getAccessToken(), authToken.getRefreshToken());

        // AccessToken과 RefreshToken을 LoginResponseDto에 담아서 반환합니다.
        return new LoginResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }


    // Access Token가 만료일자만 초과한 토큰인지 검사
    public boolean validate(String requestAccessTokenInHeader) {
        try {
            // Access Token의 유효성 검사
            boolean isValidAccessToken = JwtTokenProvider.validateToken(requestAccessTokenInHeader);

            // Access Token이 유효하지 않은 경우에도 Refresh Token의 유효성 검사
            if (!isValidAccessToken) {
                String requestRefreshToken = extractRefreshToken(requestAccessTokenInHeader);
                boolean isValidRefreshToken = JwtTokenProvider.validateRefreshToken(requestRefreshToken);
                return isValidRefreshToken;
            }

            return isValidAccessToken;
        } catch (Exception e) {
            // 토큰 검사 중에 예외 발생 시 유효하지 않은 토큰으로 처리
            return false;
        }
    }



    // 토큰 재발급: validate가 true일 때 access, refresh 모두 재발급
    @Transactional
    public TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        boolean isValidAccessToken = JwtTokenProvider.validateToken(requestAccessTokenInHeader);
        boolean isValidRefreshToken = JwtTokenProvider.validateRefreshToken(requestRefreshToken);

        if (isValidAccessToken && isValidRefreshToken) {
            // Access Token에서 유저 정보 추출
            Authentication authentication = JwtTokenProvider.getAuthentication(requestAccessTokenInHeader);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userId = userDetails.getUsername();

            // 새로운 Access Token과 Refresh Token 발급
            TokenDto newAuthToken = JwtTokenProvider.createToken(userId, authentication);

            // 새로운 TokenDto 반환
            return newAuthToken;
        }

        // 유효하지 않은 토큰이거나 Refresh Token이 유효하지 않은 경우 null 반환
        return null;
    }


    // 토큰 발급
    @Transactional
    public TokenDto generateToken(String provider, String id, String authorities) {
        // 사용자 권한 정보를 ','로 구분하여 String 배열로 변환
        String[] authorityArr = authorities.split(",");

        // 권한들을 SimpleGrantedAuthority로 변환하여 리스트 생성
        List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorityArr)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체 생성
        UserDetails userDetails = new User(id, "", grantedAuthorities);

        // UsernamePasswordAuthenticationToken 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", grantedAuthorities);

        // 토큰 발급
        TokenDto authToken = JwtTokenProvider.createToken(id, authentication);

        // 발급된 TokenDto 반환
        return authToken;
    }


    // RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        // 저장할 Redis 키를 생성합니다. 여기서는 "refresh_token:provider:principal" 형태로 지정합니다.
        String redisKey = "refresh_token:" + provider + ":" + principal;

        // refreshToken을 Redis에 저장합니다.
        redisService.setValues(redisKey, refreshToken);

        // refreshToken의 만료 시간을 설정해주는 것이 일반적이며, 실제 사용 시에는 해당 기능도 추가해야합니다.
        // 만료 시간을 설정하지 않으면 refreshToken은 Redis에 영구히 저장됩니다.
        // 예를 들어, redisService.set(redisKey, refreshToken, 만료시간) 과 같이 만료 시간을 설정합니다.
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        // Authentication 객체에서 권한들을 추출합니다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 권한 이름들을 ","로 구분하여 하나의 문자열로 변환합니다.
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }



    // AT로부터 principal 추출
    public String getPrincipal(String requestAccessToken) {
        String tokenPrefix = "Bearer ";
        if (requestAccessToken != null && requestAccessToken.startsWith(tokenPrefix)) {
            String token = requestAccessToken.substring(tokenPrefix.length());
            Claims claims = Jwts.parserBuilder().setSigningKey(JwtTokenProvider.getSigningKey()).build()
                    .parseClaimsJws(token).getBody();
            return claims.getSubject();
        }
        return null;
    }


    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessTokenInHeader) {
        String bearerPrefix = "Bearer ";
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith(bearerPrefix)) {
            return requestAccessTokenInHeader.substring(bearerPrefix.length());
        }
        // "Bearer "로 시작하지 않거나, 토큰 부분이 없는 경우 null을 반환합니다.
        return null;
    }



    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        // Access Token을 무효화하여 로그아웃 처리
        JwtTokenProvider.invalidateToken(requestAccessTokenInHeader);

        // (선택사항) Redis 또는 다른 저장소에서 관련 정보 삭제
        // redisService.deleteValues(requestAccessTokenInHeader);
        // 또는 다른 저장소에 저장된 토큰 정보 삭제 등의 작업을 수행할 수 있습니다.
    }

    ////////////////

    public boolean findMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean findMemberByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    private String extractRefreshToken(String requestAccessTokenInHeader) {
        String tokenPrefix = "Bearer ";
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith(tokenPrefix)) {
            return requestAccessTokenInHeader.substring(tokenPrefix.length());
        }
        return null;
    }

    // Refresh Token 유효성 검사
    public boolean validateRefreshToken(String requestRefreshToken) {
        try {
            // Refresh Token의 유효성 검사
            return JwtTokenProvider.validateRefreshToken(requestRefreshToken);
        } catch (Exception e) {
            // 예외 발생 시 유효하지 않은 토큰으로 처리
            return false;
        }
    }

}