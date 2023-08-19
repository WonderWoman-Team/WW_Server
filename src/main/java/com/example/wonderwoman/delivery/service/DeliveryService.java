package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.PostStatus;
import com.example.wonderwoman.delivery.entity.SanitarySize;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.delivery.repository.DeliveryRepositoryImpl;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.response.DeliveryResponseDto;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryPostRepository deliveryPostRepository;
    private final DeliveryRepositoryImpl deliveryRepositoryImpl;

    // 학교 정보를 기반으로 건물 목록 조회
    public List<Building> getBuildingsBySchool(School school) {
        List<Building> buildings = school.getBuildingList();
        System.out.println("회원의 학교: " + school + "에 해당하는 건물 목록: " + buildings);
        return buildings;
    }

    // 게시글 작성
    public void postDelivery(Member member, DeliveryRequestDto requestDto) {
        DeliveryPost deliveryPost = requestDto.toDeliveryPost(member);
        deliveryPostRepository.save(deliveryPost);
    }

    // 게시글 조회 - 전체
    public Slice<DeliveryResponseDto> getAllDeliveryPosts(Member member,
                                                          String reqType,
                                                          String school,
                                                          List<String> building,
                                                          List<String> sizeList,
                                                          Long lastId,
                                                          Pageable pageable) {

        List<SanitarySize> sanitarySizes = new ArrayList<>();

        for (String size : sizeList) {
            sanitarySizes.add(SanitarySize.resolve(size));
        }

        List<Building> buildings = new ArrayList<>();

        for (String b : building) {
            buildings.add(Building.resolve(b));
        }

        return deliveryRepositoryImpl.getSliceOfDelivery(member, reqType, school, buildings, sanitarySizes, lastId, pageable);
    }

    // 게시글 상태 조회
    public PostStatus findPostStatus(Member member, String postId) {
        DeliveryPost deliveryPost = deliveryPostRepository.findByIdAndMember(Long.valueOf(postId), member)
                .orElseThrow(() -> new RuntimeException("해당하는 게시글을 찾을 수 없습니다."));
        return deliveryPost.getPostStatus();
    }

    @Transactional
    public void updatePostStatusWithCancellationByPostId(Member member, Long postId) {
        DeliveryPost deliveryPost = deliveryPostRepository.findById(postId)
                .orElseThrow(() -> new WonderException(ErrorCode.ARTICLE_NOT_FOUND));

        if (!deliveryPost.isWrittenPost(member))
            throw new WonderException(ErrorCode.FORBIDDEN_ARTICLE);

        // 이미 '없음' 상태인 경우에는 게시글 상태를 postStatus로 변경
        if (deliveryPost.getPostStatus().equals(PostStatus.NONE)) {
            deliveryPost.updatePostStatus(PostStatus.CANCEL);
            deliveryPostRepository.save(deliveryPost);
        } else {
            throw new WonderException(ErrorCode.INVALID_POST_STATUS_CHANGE);
        }
    }


}
