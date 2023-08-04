package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.entity.ListResult;
import com.example.wonderwoman.delivery.entity.PostStatus;
import com.example.wonderwoman.delivery.entity.SanitarySize;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponse {
    private String id;

    private Long userId;

    private Long callerId;

    private String callerNickName;

    private String callerImg;

    private Long helperId;

    private String helperNickName;

    private String helperImg;

    private PostStatus status;

    private School school;

    private Building building;

    private SanitarySize sanitarySize;

    private LocalDateTime createdAt;

    private ListResult<ChatMessage> messages;

    public static ChatRoomResponse of(ChatRoom chatRoom, Member member, ListResult<ChatMessage> messages) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .userId(member.getId())
                .callerId(chatRoom.getCaller().getId())
                .callerNickName(chatRoom.getCaller().getNickname())
                .callerImg(chatRoom.getCaller().getImgUrl())
                .helperId(chatRoom.getHelper().getId())
                .helperNickName(chatRoom.getHelper().getNickname())
                .helperImg(chatRoom.getHelper().getImgUrl())
                .status(chatRoom.getDeliveryPost().getPostStatus())
                .school(chatRoom.getDeliveryPost().getSchool())
                .building(chatRoom.getDeliveryPost().getBuilding())
                .sanitarySize(chatRoom.getDeliveryPost().getSanitarySize())
                .createdAt(chatRoom.getJoinedAt())
                .messages(messages)
                .build();
    }
}
