package com.example.wonderwoman.jwt;

import com.example.wonderwoman.auth.service.RedisService;
import com.example.wonderwoman.common.dto.TokenDto;
import com.example.wonderwoman.login.PrincipalDetails;
import com.example.wonderwoman.member.entity.Member;
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

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.EMAIL;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";

    private static final String ID = "id";
    private static Key signingKey;
    private static Long validityMsec;
    private static Long refreshMsec;
    public static  RedisService redisService;
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
    // Token 생성
    public static TokenDto createToken(String email, Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityMsec);
        Date refresh = new Date(now.getTime() + refreshMsec);

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject("access-token")   // 사용자 정보가 지정될 것
                .claim(AUTHORITIES_KEY, authorities)
                .claim(String.valueOf(EMAIL), email)          // 이메일 정보를 클레임에 추가
                .setIssuedAt(now)             // 발급 시간
                .setExpiration(expiration)    // 만료 시간
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject("refresh-token")
                .setIssuedAt(now)
                .setExpiration(refresh)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();

        // TokenDto에 Access Token과 Refresh Token 정보 담기
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    //토큰으로 인증 정보 조회
    public static Authentication getAuthentication(String token) {
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

        String id = claims.get(ID).toString();

        //UserDetails 객체에 토큰 정보와 생성한 인가 넣고 return
        UserDetails userDetails = new PrincipalDetails(Member.builder().build());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public static boolean validateToken(String token) {
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

    public static boolean validateRefreshToken(String token) {
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

    private static Claims parseClaims(String accessToken) {
        try {
            //올바른 토큰이면 true
            return Jwts.parserBuilder().setSigningKey(signingKey).build()
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

    public static Key getSigningKey() {
        return signingKey;
    }

    public static void invalidateToken(String token) {
        // Redis 또는 다른 저장소를 사용하는 경우 해당 토큰을 무효화하는 로직을 구현합니다.
        // 예를 들어 Redis에 저장된 토큰 정보를 삭제하는 코드 등을 작성할 수 있습니다.
        // redisService.deleteValues(token);

        // 여기서는 단순히 로그만 남기는 예시를 보여줍니다.
        log.info("Token invalidated: {}", token);
    }


}
