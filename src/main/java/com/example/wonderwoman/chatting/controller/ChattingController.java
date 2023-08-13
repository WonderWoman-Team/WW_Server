package com.example.wonderwoman.chatting.controller;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.repository.ChatMessageRepository;
import com.example.wonderwoman.chatting.repository.ChatRoomRepository;
import com.example.wonderwoman.chatting.service.ChatService;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin
@Transactional
public class ChattingController {
    private final SimpMessagingTemplate template;
    private final SimpMessageSendingOperations sendingOperations;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    //메시지 처리
    @MessageMapping("/message")
    public void message(SendMessage sendMessage) {
        ChatRoom chatRoom = chatRoomRepository.findById(sendMessage.getRoomId())
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));

        ChatMessage message;

        if (ChatMessage.MessageType.ENTER.equals(sendMessage.getType())) {
            message = createEnterMessage(chatRoom, sendMessage.getType());
            sendMessage.setMessage(message.getMessage());
        } else {
            Member member = memberRepository.findById(sendMessage.getSenderId())
                    .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
            message = createTalkMessage(chatRoom, member, sendMessage.getType(), sendMessage.getMessage());
            sendMessage.setMessage(message.getMessage());
        }


        chatMessageRepository.save(message);
        chatRoom.addChatMessage(message);
        chatRoom.updateLastMessage(message.getMessage());
        chatRoomRepository.save(chatRoom);

        if (message.getType().equals(ChatMessage.MessageType.TALK)) {
            sendMessage.setSenderId(message.getSender().getId());
            sendMessage.setSenderNickname(message.getSender().getNickname());
            sendMessage.setImgUrl(message.getSender().getImgUrl());
        }
        sendMessage.setSendTime(message.getSendDate());
        sendingOperations.convertAndSend("/sub/chat/room/" + message.getChatRoom().getId(), sendMessage);
    }


    private ChatMessage createEnterMessage(ChatRoom chatRoom, ChatMessage.MessageType messageType) {
        ReqType reqType = chatRoom.getDeliveryPost().getPostReqType();
        Member member = reqType.equals(ReqType.REQUEST) ? chatRoom.getHelper() : chatRoom.getCaller();
        String messageContent = reqType.equals(ReqType.REQUEST) ?
                "당신의 그 날을 지켜드릴게요! 딜리버리 출동하겠습니다. " + member.getNickname() + "님이 딜리버리 요청에 응답했습니다." :
                "난감한 그 날을 지켜주세요! " + member.getNickname() + "님이 딜리버리 요청에 응답했습니다.";

        return ChatMessage.builder()
                .sender(null)
                .chatRoom(chatRoom)
                .messageType(messageType.name())
                .message(messageContent)
                .build();
    }

    private ChatMessage createTalkMessage(ChatRoom chatRoom, Member sender, ChatMessage.MessageType messageType, String messageContent) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .messageType(messageType.name())
                .message(messageContent)
                .build();
    }


    @Data
    private static class SendMessage {
        ChatMessage.MessageType type;
        String roomId;
        String message;
        Long senderId;
        String senderNickname;
        String imgUrl;
        LocalDateTime sendTime;
    }
}
