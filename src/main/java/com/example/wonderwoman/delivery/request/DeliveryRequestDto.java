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

    @NotBlank(message = "요청 또는 출동을 선택하세요.")
    private ReqType reqType;

    @NotBlank(message = "개수는 필수 입력값입니다.")
    private int postNumber;

    @NotBlank(message = "크기는 필수 입력값입니다.")
    private SanitarySize sanitarySize;

    @NotBlank(message = "종류는 필수 입력값입니다.")
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