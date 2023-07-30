package com.example.wonderwoman.mail.service;

import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import com.example.wonderwoman.member.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponseDto getMyInfo(Member currentMember) {
        return memberRepository.findById(currentMember.getId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
