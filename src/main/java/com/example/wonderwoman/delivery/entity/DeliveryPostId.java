package com.example.wonderwoman.delivery.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.io.Serializable;

@Embeddable
public class DeliveryPostId implements Serializable {
    //유저 id
    private Long id;

    //게시물 id
    private Long postId;

}