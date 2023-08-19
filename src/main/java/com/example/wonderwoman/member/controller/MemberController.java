package com.example.wonderwoman.member.controller;

import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.request.ChangeMemberInfoRequest;
import com.example.wonderwoman.member.response.MemberResponseDto;
import com.example.wonderwoman.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/myInfo")
    public ResponseEntity<MemberResponseDto> changeMyInfo(@CurrentUser Member member, @RequestBody ChangeMemberInfoRequest request) {
        memberService.changeMemberInfo(member, request);
        return ResponseEntity.ok(memberService.getMyInfo(member));
    }
}
