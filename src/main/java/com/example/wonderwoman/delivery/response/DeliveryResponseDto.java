package com.example.wonderwoman.delivery.response;

import com.example.wonderwoman.delivery.entity.*;
import com.example.wonderwoman.member.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryResponseDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String createdAt;
    private School school;
    private List<Building> building;
    private PostStatus postStatus;
    private String postTitle;
    private ReqType postReqType;
    private int sanitaryNum;
    private SanitarySize sanitarySize;
    private SanitaryType sanitaryType;
    private String postComment;
    private boolean isWritten;

    public static DeliveryResponseDto of(DeliveryPost deliveryPost, boolean isWritten) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtString = deliveryPost.getJoinedAt().format(formatter);

        return DeliveryResponseDto.builder()
                .id(deliveryPost.getId())
                .memberId(deliveryPost.getMember().getId())
                .nickname(deliveryPost.getMember().getNickname())
                .createdAt(createdAtString)
                .school(deliveryPost.getSchool())
                .building(deliveryPost.getBuilding())
                .postStatus(deliveryPost.getPostStatus())
                .postTitle(deliveryPost.getPostTitle())
                .postReqType(deliveryPost.getPostReqType())
                .sanitaryNum(deliveryPost.getSanitaryNum())
                .sanitarySize(deliveryPost.getSanitarySize())
                .sanitaryType(deliveryPost.getSanitaryType())
                .postComment(deliveryPost.getPostComment())
                .isWritten(isWritten)
                .build();
    }
}
