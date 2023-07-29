package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.entity.ListResult;
import com.example.wonderwoman.delivery.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponse {
    private String id;

    private Long callerId;

    private String callerNickName;

    private Long helperId;

    private String helperNickName;

    private PostStatus status;

    private ListResult<ChatMessage> messages;

    public static ChatRoomResponse of(ChatRoom chatRoom, ListResult<ChatMessage> messages) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .callerId(chatRoom.getCaller().getId())
                .callerNickName(chatRoom.getCaller().getNickname())
                .helperId(chatRoom.getHelper().getId())
                .helperNickName(chatRoom.getHelper().getNickname())
                .status(chatRoom.getPost().getPostStatus())
                .messages(messages)
                .build();
    }
}
