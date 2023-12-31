package com.example.wonderwoman.member.entity;

import com.example.wonderwoman.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

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

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Builder
    public Member(String email, String nickname, String password, School school) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.school = school;
        this.role = Role.ROLE_USER;
        this.imgUrl = null;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateImage(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
