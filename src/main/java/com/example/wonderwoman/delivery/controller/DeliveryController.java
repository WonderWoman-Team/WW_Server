package com.example.wonderwoman.delivery.controller;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.response.DeliveryResponseDto;
import com.example.wonderwoman.delivery.service.DeliveryService;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.member.entity.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@AllArgsConstructor
@RequestMapping("/app/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    //딜리버리 게시판

    //딜리버리 게시글 작성
    @PostMapping("")
    public ResponseEntity<NormalResponseDto> postDelivery(@CurrentUser Member member, @RequestBody @Valid DeliveryRequestDto requestDto) {
        requestDto.setSchool(member.getSchool());

        Building selectedBuilding = requestDto.getBuilding();

        List<Building> buildings = deliveryService.getBuildingsBySchool(member.getSchool());

        // 사용자가 선택한 건물이 올바른지 확인
        if (!buildings.contains(selectedBuilding)) {
            throw new WonderException(ErrorCode.BUILDING_NOT_MATCH);
        }

        deliveryService.postDelivery(member, requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    // 딜리버리 게시글 조회 - 전체
    @GetMapping("/post")
    public ResponseEntity<Slice<DeliveryResponseDto>> getAllDeliveryPosts(@CurrentUser Member member,
                                                                          @RequestParam(value = "reqType", required = false) String reqType,
                                                                          @RequestParam(value = "school", required = false) String school,
                                                                          @RequestParam(value = "building", required = false) String building,
                                                                          @RequestParam(value = "size", defaultValue = "") List<String> sizeList,
                                                                          @PageableDefault(sort = "joinedAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.getAllDeliveryPosts(member, reqType, school, building, sizeList, pageable));
    }

}
