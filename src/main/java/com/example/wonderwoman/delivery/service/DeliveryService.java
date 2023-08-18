package com.example.wonderwoman.delivery.service;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.SanitarySize;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.delivery.repository.DeliveryRepositoryImpl;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.response.DeliveryResponseDto;
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



}
