package com.example.wonderwoman.chatting.repository;

import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    List<ChatRoom> findChatRoomsByCaller(Member caller);

    List<ChatRoom> findChatRoomsByHelper(Member helper);

    @Query("select cr from ChatRoom cr where cr.caller.id =: id or cr.helper.id =: id")
    List<ChatRoom> findChatRoomsByMember(@Param("idr") Long id);

    @Modifying
    @Query("update ChatRoom cr set cr.lastMessage =: message where cr.id =: id")
    void updateLastMessage(@Param("id") Long chatRoomId, @Param("message") String message);

}
