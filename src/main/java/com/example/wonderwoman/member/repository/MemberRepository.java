package com.example.wonderwoman.member.repository;

import com.example.wonderwoman.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);    //중복 가입 방지 위함

    boolean existsByNickname(String nickname);  //중복 닉네임 방지 위함

}
