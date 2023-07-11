package com.example.wonderwoman.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @Column(name = "email", length = 200)
    private String email;

    @NotNull
    @Column(name = "nickname", length = 20)
    private String nickname;

    @NotNull
    @Column(name = "password", length = 100)
    private String password;

    @NotNull
    @Column(name = "school", length = 20)
    @Enumerated(EnumType.STRING)
    private School school;

    @Builder
    public Member(String email, String nickname, String password, School school) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.school = school;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
