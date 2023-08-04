package com.example.wonderwoman.delivery.request;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.delivery.entity.*;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryRequestDto {
    private School school;

    private Building building;

    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String postTitle;
    
    private ReqType postReqType;

    private int sanitaryNum;

    private SanitarySize sanitarySize;

    private SanitaryType sanitaryType;

    private String postComment;

    public DeliveryPost toDeliveryPost(Member member) {
        return DeliveryPost.builder()
                .school(school)
                .building(building)
                .postStatus(PostStatus.NONE)
                .postTitle(postTitle)
                .postReqType(postReqType)
                .sanitaryNum(sanitaryNum)
                .sanitarySize(sanitarySize)
                .sanitaryType(sanitaryType)
                .postComment(postComment)
                .member(member)
                .build();
    }
}