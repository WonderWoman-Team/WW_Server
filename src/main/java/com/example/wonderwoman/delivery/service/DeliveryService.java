package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.DeliveryPostResponseDto;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
//    public List<DeliveryPostResponseDto> getAllDeliveryPosts() {
//        List<DeliveryPost> deliveryPosts = deliveryPostRepository.findAll();
//        // DeliveryPost를 DeliveryPostResponseDto로 변환하여 리스트로 반환
//        return deliveryPosts.stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }

    // 게시글 조회 - 유형:요청
    public List<DeliveryPost> getDeliveryPostsByTypeRequest() {
        return deliveryPostRepository.findByPostReqType(ReqType.REQUEST.getTypeName());
    }

    // 게시글 조회 - 유형:출동
    public List<DeliveryPost> getDeliveryPostsByTypeDispatch() {
        return deliveryPostRepository.findByPostReqType(ReqType.DISPATCH.getTypeName());
    }

    // DeliveryPost를 DeliveryPostResponseDto로 변환하는 메서드
    private DeliveryPostResponseDto convertToResponseDto(DeliveryPost deliveryPost) {
        return new DeliveryPostResponseDto(
                deliveryPost.getPostTitle(),
                deliveryPost.getPostReqType().getTypeName(),
                deliveryPost.getPostNumber(),
                deliveryPost.getSanitarySize().getSizeName(),
                deliveryPost.getSanitaryType().getTypeName(),
                deliveryPost.getPostStatus().getStatusName()
        );
    }

}
