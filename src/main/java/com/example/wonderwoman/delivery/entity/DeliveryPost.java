package com.example.wonderwoman.delivery.entity;

import com.example.wonderwoman.member.entity.Member;
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

}