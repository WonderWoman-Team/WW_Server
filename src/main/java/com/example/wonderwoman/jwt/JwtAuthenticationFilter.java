package com.example.wonderwoman.jwt;

import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.login.service.JwtTokenProvider;
import io.jsonwebtoken.IncorrectClaimException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        //AT 추출
        String token = resolveToken(request);

        try {
            //유효기간만 제외하고 정상토큰인지 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (IncorrectClaimException e) {   //잘못된 토큰
            SecurityContextHolder.clearContext();
            throw new WonderException(ErrorCode.INVALID_TOKEN);
        } catch (RedisConnectionFailureException e) {   //레디스 연결 안 될때
            SecurityContextHolder.clearContext();
            throw new WonderException(ErrorCode.REDIS_ERROR);
        } catch (UsernameNotFoundException e) { //회원 못 찾을 때
            throw new WonderException(ErrorCode.MEMBER_NOT_FOUND);
        }

        filterChain.doFilter(request, response);
    }

    //request header에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
