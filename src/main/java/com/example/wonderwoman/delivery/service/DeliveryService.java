package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryPostRepository deliveryPostRepository;

    //회원가입
    public void postDelivery(Member member, DeliveryRequestDto requestDto) {
        DeliveryPost deliveryPost = requestDto.toDeliveryPost(member);
        deliveryPostRepository.save(deliveryPost);
    }

}
