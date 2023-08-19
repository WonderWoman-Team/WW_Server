package com.example.wonderwoman.member.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeMemberInfoRequest {
    private String nickname;

    private String password;

    private String imgUrl;
}
