package com.example.wonderwoman.delivery.controller;

import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.service.DeliveryService;
import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.member.entity.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/app/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    //딜리버리 게시판

    //딜리버리 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<NormalResponseDto> postDelivery(@CurrentUser Member member, @RequestBody @Valid DeliveryRequestDto requestDto) {
        deliveryService.postDelivery(member, requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }

    // 딜리버리 게시글 조회 - 전체
    @GetMapping("/post")

    @ResponseBody
    public ResponseEntity<List<DeliveryPost>> getAllDeliveryPosts() {
        List<DeliveryPost> deliveryPosts = deliveryService.getAllDeliveryPosts();
        return ResponseEntity.ok(deliveryPosts);
    }

//    public ResponseEntity<List<DeliveryPostResponseDto>> getAllDeliveryPosts() {
//        List<DeliveryPostResponseDto> deliveryPosts = deliveryService.getAllDeliveryPosts();
//        return ResponseEntity.ok(deliveryPosts);
//    }

//    public ResponseEntity<List<DeliveryResponseDto>> getAllDeliveryPosts() {
//        List<DeliveryPost> deliveryPosts = deliveryService.getAllDeliveryPosts();
//        List<DeliveryResponseDto> responseDtos = new ArrayList<>();
//        for (DeliveryPost deliveryPost : deliveryPosts) {
//            DeliveryResponseDto responseDto = DeliveryResponseDto.of(deliveryPost, false);
//            responseDtos.add(responseDto);
//        }
//        return ResponseEntity.ok(responseDtos);
//    }

    // 딜리버리 게시글 조회 - 유형: 요청
    @GetMapping(params = "category=request")
    @ResponseBody
    public ResponseEntity<List<DeliveryPost>> getDeliveryPostsByTypeRequest() {
        List<DeliveryPost> requestDeliveryPosts = deliveryService.getDeliveryPostsByTypeRequest();
        return ResponseEntity.ok(requestDeliveryPosts);
    }

    // 딜리버리 게시글 조회 - 유형: 출동
    @GetMapping(params = "category=dispatch")
    @ResponseBody
    public ResponseEntity<List<DeliveryPost>> getDeliveryPostsByTypeDispatch() {
        List<DeliveryPost> dispatchDeliveryPosts = deliveryService.getDeliveryPostsByTypeDispatch();
        return ResponseEntity.ok(dispatchDeliveryPosts);
    }

}
