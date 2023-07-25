package com.example.wonderwoman.delivery.request;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.DeliveryPostId;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    private DeliveryPostId deliveryPostId;
    private Member member;
    private String schoolId;
    private String schoolBuildingId;
    private String postStatus;


    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String post_title;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String post_req_type;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String post_number;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String post_size;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String post_type;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String post_comment;

    private String school;

    public DeliveryPost toDeliveryPost() {
        return DeliveryPost.builder()
                .deliveryPostId(deliveryPostId)
                .schoolId(schoolId)
                .school_building_id(schoolBuildingId)
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
