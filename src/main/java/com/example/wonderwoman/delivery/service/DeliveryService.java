package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DeliveryService {

    @Autowired
    private DeliveryPostRepository deliveryPostRepository;

    //회원가입
    public void postDelivery(DeliveryRequestDto requestDto) {
        DeliveryPost deliveryPost = requestDto.toDeliveryPost();
        deliveryPostRepository.save(deliveryPost);
    }

}
