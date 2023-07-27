package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryPostRepository deliveryPostRepository;

    // 게시글 작성
    public void postDelivery(Member member, DeliveryRequestDto requestDto) {
        DeliveryPost deliveryPost = requestDto.toDeliveryPost(member);
        deliveryPostRepository.save(deliveryPost);
    }

    // 게시글 조회 - 전체
    public List<DeliveryPost> getAllDeliveryPosts() {
        return deliveryPostRepository.findAll();
    }

    // 게시글 조회 - 유형:요청
    public List<DeliveryPost> getDeliveryPostsByTypeRequest() {
        return deliveryPostRepository.findByPostReqType("request");
    }

    // 게시글 조회 - 유형:출동
    public List<DeliveryPost> getDeliveryPostsByTypeDispatch() {
        return deliveryPostRepository.findByPostReqType("dispatch");
    }

}
