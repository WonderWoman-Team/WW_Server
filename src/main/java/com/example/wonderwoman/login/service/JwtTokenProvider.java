package com.example.wonderwoman.login.service;

import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.security.PrincipalDetails;
import com.example.wonderwoman.security.PrincipalDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Component
@Transactional(readOnly = true)
@Slf4j
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "role";
    private static final String EMAIL_KEY = "email";
    private final Key signingKey;
    private final Long accessTokenValidTime;
    private final Long refreshTokenValidTime;
    private final RedisService redisService;
    private final PrincipalDetailsService principalDetailsService;

    public JwtTokenProvider(
            PrincipalDetailsService principalDetailsService,
            RedisService redisService,
            @Value("${jwt.token.secret}") String secretKey,
            @Value("${jwt.token.access-token-validity-in-seconds}") Long accessTokenValidTime,
            @Value("${jwt.token.refresh-token-validity-in-seconds}") Long refreshTokenValidTime
    ) {
        this.principalDetailsService = principalDetailsService;
        this.redisService = redisService;
        this.accessTokenValidTime = accessTokenValidTime * 1000L;
        this.refreshTokenValidTime = refreshTokenValidTime * 1000L;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public long getTokenExpirationTime(String token) {
        return parseClaims(token).getExpiration().getTime();
    }

    //filter에서 사용
    public boolean validateToken(String token) {
        //확인 위해 아래와 같이 작성
        try {
            if (redisService.getValues(token) != null
                    && redisService.getValues(token).equals("logout")) {
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.");
            return false;
        } catch (ExpiredJwtException e) {   //만료만 된 토큰이어도 true
            log.info("만료된 JWT 토큰입니다.");
            return true;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        //확인 위해 아래와 같이 작성
        try {
            
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            return false;
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            //올바른 토큰이면 true
            return Jwts.parserBuilder().setSigningKey(signingKey).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            //만료 토큰이어도 토큰 정보 꺼내서 return
            return e.getClaims();
        }
    }

    //재발급 검증 시 사용
    public boolean validateTokenOnlyExpired(String token) {
        try {
            return parseClaims(token)
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //토큰 생성
    // Token 생성
    public TokenDto createToken(String email, String authorities) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidTime);
        Date refresh = new Date(now.getTime() + refreshTokenValidTime);

        //TokenDto에 AT, RT 생성해 담기
        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setSubject("access-token")
                .claim(EMAIL_KEY, email)
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setSubject("refresh-token")
                .setIssuedAt(now)
                .setExpiration(refresh)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token) {
        String email = parseClaims(token).get(EMAIL_KEY).toString();
        PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(principalDetails, "",
                principalDetails.getAuthorities());
    }
}
