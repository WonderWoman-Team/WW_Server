package com.example.wonderwoman.delivery.request;

import com.example.wonderwoman.building.entity.Building;
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
    //DeliveryPost 엔티티에서 자동 생성되므로 삭제
    //private DeliveryPostId deliveryPostId;
    private School school;
    private Building building;


    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String postTitle;
    
    private ReqType reqType;

    private int postNumber;

    private SanitarySize sanitarySize;

    private SanitaryType sanitaryType;

    private String postComment;

    public DeliveryPost toDeliveryPost(Member member) {
        return DeliveryPost.builder()
                .school(school)
                .building(building)
                .postStatus(PostStatus.NONE)
                .postTitle(postTitle)
                .postReqType(reqType)
                .postNumber(postNumber)
                .sanitarySize(sanitarySize)
                .sanitaryType(sanitaryType)
                .postComment(postComment)
                .member(member)
                .build();
    }
}