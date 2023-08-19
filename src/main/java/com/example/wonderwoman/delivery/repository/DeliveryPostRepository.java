package com.example.wonderwoman.delivery.repository;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DeliveryPostRepository extends JpaRepository<DeliveryPost, Long> {

    // 게시글 유형에 따른 게시물 조회
    List<DeliveryPost> findByPostReqType(ReqType postReqType);

    Optional<DeliveryPost> findByIdAndMember(Long id, Member member);

    Optional<DeliveryPost> findById(Long id);
}
