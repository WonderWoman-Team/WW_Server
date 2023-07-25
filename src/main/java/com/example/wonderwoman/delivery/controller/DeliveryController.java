package com.example.wonderwoman.delivery.controller;

import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.delivery.request.DeliveryRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.wonderwoman.delivery.service.DeliveryService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/app/board")
public class DeliveryController {

    private final DeliveryService deliveryService;

    //딜리버리 게시판

    //딜리버리 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<NormalResponseDto> postDelivery(@RequestBody @Valid DeliveryRequestDto requestDto) {
        deliveryService.postDelivery(requestDto);
        return ResponseEntity.ok(NormalResponseDto.success());
    }



}
