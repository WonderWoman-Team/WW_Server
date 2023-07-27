package com.example.wonderwoman.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_USER("회원"),
    ROLE_ANONYMOUS("익명"),
    ROLE_ADMIN("관리자");

    private final String roleName;
}
