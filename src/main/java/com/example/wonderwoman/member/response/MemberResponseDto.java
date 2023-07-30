package com.example.wonderwoman.member.response;

import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;

    private String email;

    private String nickname;

    private School school;

    private String imgUrl;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .school(member.getSchool())
                .nickname(member.getNickname())
                .imgUrl(member.getImgUrl())
                .build();
    }
}
