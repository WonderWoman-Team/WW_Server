package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.chatting.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomInfoResponse {

    private String id;

    private Long callerId;

    private String callerNickName;

    private String callerImg;

    private Long helperId;

    private String helperNickName;

    private String helperImg;

    public static ChatRoomInfoResponse of(ChatRoom chatRoom) {
        return ChatRoomInfoResponse.builder()
                .id(chatRoom.getId())
                .callerId(chatRoom.getCaller().getId())
                .callerNickName(chatRoom.getCaller().getNickname())
                .callerImg(chatRoom.getCaller().getImgUrl())
                .helperId(chatRoom.getHelper().getId())
                .helperNickName(chatRoom.getHelper().getNickname())
                .helperImg(chatRoom.getHelper().getImgUrl())
                .build();
    }
}
