package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;

    private String messageType;

    private Long senderId;

    private String senderNickname;

    private String senderImg;

    private String message;

    private String roomId;

    public static ChatMessageResponse of(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .messageType(chatMessage.getType().toString())
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .senderImg(chatMessage.getSender().getImgUrl())
                .message(chatMessage.getMessage())
                .roomId(chatMessage.getChatRoom().getId())
                .build();
    }
}
