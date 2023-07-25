package com.example.wonderwoman.delivery.entity;

import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.Role;
import com.example.wonderwoman.member.entity.School;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "delivery_post")
public class DeliveryPost {

    @EmbeddedId
    private DeliveryPostId deliveryPostId;

    @Column(name = "school", length = 10, nullable = false)
    private String schoolId;

    @Column(name = "school_building_id", length = 100, nullable = false)
    private String schoolBuildingId;

    @Column(name = "post_status", length = 10, nullable = false)
    private String postStatus;

    @Column(name = "post_title", length = 200)
    private String postTitle;

    @Column(name = "post_req_type", length = 10, nullable = false)
    private String postReqType;

    @Column(name = "post_number", nullable = false)
    private int postNumber;

    @Column(name = "post_size", length = 100, nullable = false)
    private String postSize;

    @Column(name = "post_type", length = 100, nullable = false)
    private String postType;

    @Column(name = "post_comment", length = 200)
    private String postComment;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member member;

    @Builder
    public DeliveryPost(DeliveryPostId deliveryPostId, String schoolId, String school_building_id, String post_status,
                        String post_title, String post_req_type, String post_number, String post_size,
                        String post_type, String post_comment, Member member) {
        this.deliveryPostId = deliveryPostId;
        this.schoolId = schoolId;
        this.schoolBuildingId = schoolBuildingId;
        this.postStatus = postStatus;
        this.postTitle = postTitle;
        this.postReqType = postReqType;
        this.postNumber = postNumber;
        this.postSize = postSize;
        this.postType = postType;
        this.postComment = postComment;
        this.member= member;

    }

}