package com.example.wonderwoman.delivery.request;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.DeliveryPostId;
import com.example.wonderwoman.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryRequestDto {
    //DeliveryPost 엔티티에서 자동 생성되므로 삭제
    //private DeliveryPostId deliveryPostId;
    private Member member;
    private String schoolId;
    private String schoolBuildingId;
    private String postStatus;

    @NotBlank(message = "제목은 입력값입니다.")
    private String post_title;

    @NotBlank(message = "유형은 필수 입력값입니다.")
    private String post_req_type;

    @NotBlank(message = "개수는 필수 입력값입니다.")
    private String post_number;

    @NotBlank(message = "크기는 필수 입력값입니다.")
    private String post_size;

    @NotBlank(message = "종류는 필수 입력값입니다.")
    private String post_type;

    private String post_comment;
    private String school;

    public DeliveryPost toDeliveryPost() {
        return DeliveryPost.builder()
                .schoolId(schoolId)
                .schoolBuildingId(schoolBuildingId)
                .post_status(postStatus)
                .post_title(post_title)
                .post_req_type(post_req_type)
                .post_number(post_number)
                .post_size(post_size)
                .post_type(post_type)
                .post_comment(post_comment)
                .member(member)
                .build();
    }
}
