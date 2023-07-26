package com.example.wonderwoman.delivery.entity;

import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.Role;
import com.example.wonderwoman.member.entity.School;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Table(name = "delivery_post")
public class DeliveryPost {

    //유저 id, 게시물 id 포함하고 있는 deliveryPostId
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private DeliveryPostId deliveryPostId;

    //빌딩 entity 쓰는 것보다 이렇게 학교 id, 건물 id 따로 하는게 나은지?
    @Column(name = "school", length = 10, nullable = false)
    private String schoolId;

    @Column(name = "schoolBuildingId", length = 100, nullable = false)
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
    public DeliveryPost(DeliveryPostId deliveryPostId, String schoolId, String schoolBuildingId, String postStatus,
                        String postTitle, String postReqType, int postNumber, String postSize,
                        String postType, String postComment, Member member) {
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