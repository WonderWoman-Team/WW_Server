package com.example.wonderwoman.delivery;

import com.example.wonderwoman.building.entity.Building;
import com.example.wonderwoman.delivery.entity.*;
import com.example.wonderwoman.member.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryResponseDto {
    private Long id;

    private Long memberId;

    private String nickname;

    private String title;

    private School school;

    private Building building;

    private PostStatus postStatus;

    private String postTitle;

    private ReqType postReqType;

    private int postNumber;

    private SanitarySize sanitarySize;

    private SanitaryType sanitaryType;

    private String postComment;

    private boolean isWritten;

    public static DeliveryResponseDto of(DeliveryPost deliveryPost, boolean isWritten) {
        return DeliveryResponseDto.builder()
                .id(deliveryPost.getId())
                .memberId(deliveryPost.getMember().getId())
                .nickname(deliveryPost.getMember().getNickname())
                .title(deliveryPost.getPostTitle())
                .school(deliveryPost.getSchool())
                .building(deliveryPost.getBuilding())
                .postStatus(deliveryPost.getPostStatus())
                .postTitle(deliveryPost.getPostTitle())
                .postReqType(deliveryPost.getPostReqType())
                .postNumber(deliveryPost.getPostNumber())
                .sanitarySize(deliveryPost.getSanitarySize())
                .sanitaryType(deliveryPost.getSanitaryType())
                .postComment(deliveryPost.getPostComment())
                .isWritten(isWritten)
                .build();
    }
}
