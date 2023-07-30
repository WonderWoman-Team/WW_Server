package com.example.wonderwoman.member.controller;

import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.mail.service.MemberService;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/myInfo")
    public ResponseEntity<MemberResponseDto> myInfo(@CurrentUser Member member) {
        MemberResponseDto responseDto = memberService.getMyInfo(member);
        return ResponseEntity.ok(responseDto);
    }
}
