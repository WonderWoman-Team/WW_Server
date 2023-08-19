package com.example.wonderwoman.member.service;

import com.example.wonderwoman.auth.service.AuthService;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import com.example.wonderwoman.member.request.ChangeMemberInfoRequest;
import com.example.wonderwoman.member.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthService authService;

    public MemberResponseDto getMyInfo(Member currentMember) {
        return memberRepository.findById(currentMember.getId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional
    public void changeMemberInfo(Member member, ChangeMemberInfoRequest request) {
        if (authService.findMemberByNickname(request.getNickname()))
            throw new WonderException(ErrorCode.DUPLICATE_NICKNAME);

        member.updateNickname(request.getNickname());

        if (request.getPassword() != null)
            member.updatePassword(passwordEncoder.encode(request.getPassword()));

        member.updateImage(request.getImgUrl());
        memberRepository.save(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다"));
    }
}
