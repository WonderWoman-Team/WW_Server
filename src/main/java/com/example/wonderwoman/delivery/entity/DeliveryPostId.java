package com.example.wonderwoman.delivery.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.io.Serializable;

@Embeddable
public class DeliveryPostId implements Serializable {
    private Long id;

    private Long postId;

}