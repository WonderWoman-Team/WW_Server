package com.example.wonderwoman.delivery.repository;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryPostRepository extends JpaRepository<DeliveryPost, Long> {

}
