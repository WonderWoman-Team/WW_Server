package com.example.wonderwoman.chatting.repository;

import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query("select c from ChatRoom c where c.id=:id")
    Optional<ChatRoom> findById(@Param("id") String id);

    List<ChatRoom> findChatRoomsByCaller(Member caller);

    List<ChatRoom> findChatRoomsByHelper(Member helper);

    @Query("select c from ChatRoom c where c.caller.id =:memberId or c.helper.id =:memberId")
    List<ChatRoom> findChatRoomsByMember(@Param("memberId") Long id);

    @Modifying
    @Query("update ChatRoom cr set cr.lastMessage =:message where cr.id =:id")
    void updateLastMessage(@Param("id") String chatRoomId, @Param("message") String message);

}
