package com.example.wonderwoman.chatting.controller;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.request.ChatMessageRequest;
import com.example.wonderwoman.chatting.service.ChatService;
import com.example.wonderwoman.chatting.service.RedisPublisher;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin
public class ChattingController {
    private final RedisPublisher redisPublisher;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    //메시지 처리
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = chatService.findRoomById(request.getChatroom_id());

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(member)
                .messageType(ChatMessage.MessageType.TALK)
                .message(request.getContent())
                .build();

        chatRoom.addChatMessage(chatMessage);
        chatRoom.updateLastMessage(chatMessage.getMessage());
        chatService.save(chatMessage);
        redisPublisher.publish(chatService.getTopic(request.getChatroom_id()), chatMessage);
    }
}
