package com.example.wonderwoman.jwt;

import com.example.wonderwoman.auth.service.RedisService;
import com.example.wonderwoman.common.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private static Key signingKey;
    private final Long validityMsec;
    private final Long refreshMsec;
    private final RedisService redisService;
    private final String secretKey;

    public JwtTokenProvider(RedisService redisService,
                            @Value("${jwt.token.secret}") String secretKey,
                            @Value("${jwt.token.access-token-validity-in-seconds}") Long validityMsec,
                            @Value("${jwt.token.refresh-token-validity-in-seconds}") Long refreshMsec) {
        this.redisService = redisService;
        this.secretKey = secretKey;
        this.validityMsec = validityMsec * 1000;
        this.refreshMsec = refreshMsec * 1000;
    }

    //토큰 생성
    public TokenDto createToken(Authentication authentication) {
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityMsec);
        Date refresh = new Date(now.getTime() + refreshMsec);

        //access token 생성
        String accessToken = Jwts.builder()
                .setSubject("access-token")   //user idx가 지정될 것
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(now)   //발급시간
                .setExpiration(expiration)  //만료시간
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject("refresh-token")
                .setExpiration(refresh)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        //token dtd에 access, refresh token 정보 담기
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰으로 인증 정보 조회
    public Authentication getAuthentication(String token) {
        //토큰 복호화
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        //claims에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //UserDetails 객체에 토큰 정보와 생성한 인가 넣고 return
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public boolean validateToken(String token) {
        //확인 위해 아래와 같이 작성
        try {
            if (redisService.getValues(token) != null // NullPointException 방지
                    && redisService.getValues(token).equals("logout")) { // 로그아웃 했을 경우
                return false;
            }
            Jwts.parserBuilder().setSigningKey(signingKey)
                    .build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
        //return !claims.getBody().getExpiration().before(new Date());
    }

    public boolean validateRefreshToken(String token) {
        //확인 위해 아래와 같이 작성
        try {
            if (redisService.getValues(token).equals("delete")) { // 회원 탈퇴했을 경우
                return false;
            }
            Jwts.parserBuilder().setSigningKey(signingKey)
                    .build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (NullPointerException e) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
        //return !claims.getBody().getExpiration().before(new Date());
    }

    private Claims parseClaims(String accessToken) {
        try {
            //올바른 토큰이면 true
            return Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            //만료 토큰이어도 토큰 정보 꺼내서 return
            return e.getClaims();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] secretKeyBytes = Base64.decodeBase64(secretKey);
        signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public boolean validateAccessTokenOnlyExpired(String accessToken) {
        try {
            return parseClaims(accessToken)
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
