package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {

    private Long id;

    private String messageType;

    private Long senderId;

    private String senderNickname;

    private String senderImg;

    private String message;

    private String roomId;

    private LocalDateTime sendTime;

    public static ChatMessageDto of(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .messageType(chatMessage.getType().toString())
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .senderImg(chatMessage.getSender().getImgUrl())
                .message(chatMessage.getMessage())
                .roomId(chatMessage.getChatRoom().getId())
                .sendTime(chatMessage.getSendDate())
                .build();
    }
}
