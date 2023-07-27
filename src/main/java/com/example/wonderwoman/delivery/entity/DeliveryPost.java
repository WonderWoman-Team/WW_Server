package com.example.wonderwoman.delivery.entity;

import com.example.wonderwoman.building.entity.Building;
import com.example.wonderwoman.common.entity.BaseTimeEntity;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delivery_post")
public class DeliveryPost extends BaseTimeEntity {

    //게시물 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private School school;

    @Enumerated(EnumType.STRING)
    private Building building;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Column(name = "post_title", length = 200)
    private String postTitle;

    @Enumerated(EnumType.STRING)
    private ReqType postReqType;

    @Column(name = "post_number", nullable = false)
    private int postNumber;

    @Enumerated(EnumType.STRING)
    private SanitarySize sanitarySize;

    @Enumerated(EnumType.STRING)
    private SanitaryType sanitaryType;

    @Column(name = "post_comment", length = 200)
    private String postComment;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "writer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member member;

    @Builder
    public DeliveryPost(School school, Building building, PostStatus postStatus,
                        String postTitle, ReqType postReqType, int postNumber, SanitarySize sanitarySize,
                        SanitaryType sanitaryType, String postComment, Member member) {
        this.school = school;
        this.building = building;
        this.postStatus = postStatus;
        this.postTitle = postTitle;
        this.postReqType = postReqType;
        this.postNumber = postNumber;
        this.sanitarySize = sanitarySize;
        this.sanitaryType = sanitaryType;
        this.postComment = postComment;
        this.member = member;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof DeliveryPost)) return false;
        DeliveryPost deliveryPost = (DeliveryPost) obj;
        return Objects.equals(id, deliveryPost.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}