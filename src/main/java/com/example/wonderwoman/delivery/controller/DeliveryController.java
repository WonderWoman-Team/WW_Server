package com.example.wonderwoman.delivery.controller;

import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import com.example.wonderwoman.delivery.service.DeliveryService;
import com.example.wonderwoman.member.entity.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/app/board")
public class DeliveryController {

    private final DeliveryService deliveryService;

    //딜리버리 게시판

    //딜리버리 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<NormalResponseDto> postDelivery(Member member, @RequestBody @Valid DeliveryRequestDto requestDto) {
        deliveryService.postDelivery(member, requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }


}
