package com.example.wonderwoman.chatting.controller;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.repository.ChatRoomRepository;
import com.example.wonderwoman.chatting.response.ChatMessageResponse;
import com.example.wonderwoman.chatting.service.ChatService;
import com.example.wonderwoman.chatting.service.RedisPublisher;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final ChatRoomRepository chatRoomRepository;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate chatRedisTemplate;

    //메시지 처리
    @MessageMapping("/chat")
    public void message(ChatMessageResponse chatMessage) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId())
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));

        ChatMessage message;

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getMessageType())) {
            chatService.enterChatRoom(chatMessage.getRoomId());
            message = ChatMessage.builder()
                    .sender(null)
                    .chatRoom(chatRoom)
                    .messageType("ENTER")
                    .message(null)
                    .build();

            if (chatRoom.getDeliveryPost().getPostReqType().equals(ReqType.REQUEST)) {
                Member member = chatRoom.getHelper();
                message.setMessage("당신의 그 날을 지켜드릴게요! 딜리버리 출동하겠습니다. " + member.getNickname() + "님이 딜리버리 요청에 응답했습니다.");
            } else {
                Member member = chatRoom.getCaller();
                message.setMessage("난감한 그 날을 지켜주세요! " + member.getNickname() + "님이 딜리버리 요청에 응답했습니다.");
            }
        } else {
            Member member = memberRepository.findById(chatMessage.getSenderId())
                    .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));

            message = ChatMessage.builder()
                    .sender(member)
                    .chatRoom(chatRoom)
                    .messageType("TALK")
                    .message(chatMessage.getMessage())
                    .build();
        }

        chatRoom.addChatMessage(message);
        chatRoom.updateLastMessage(chatMessage.getMessage());
        chatService.save(message);
        redisPublisher.publish(chatService.getTopic(chatMessage.getRoomId()), message);
    }
}
