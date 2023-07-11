package com.example.wonderwoman.config;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
@Slf4j
public class SecurityUtil {

    public static Long getCurrentMemberIdx() {
        //JwtAuthenticationFilter에서 저장한 유저 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            return 0L; // 인증 받지 못한 사용자인 경우 ID 0 을 리턴
        }

        //userIdx return
        return Long.parseLong(authentication.getName());
    }

}
