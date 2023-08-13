package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.building.entity.Building;
import com.example.wonderwoman.chatting.entity.ChatRoom;
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
public class ChatRoomInfoResponse {

    private String id;

    private Long userId;

    private String userNickName;

    private String userImg;

    private PostStatus postStatus;

    private School school;

    private Building building;

    private SanitarySize sanitarySize;

    private int sanitaryNum;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String lastMessage;

    private boolean isWriter;

    public static ChatRoomInfoResponse of(ChatRoom chatRoom, Member member, boolean isWriter) {
        return ChatRoomInfoResponse.builder()
                .id(chatRoom.getId())
                .userId(member.getId())
                .userNickName(member.getNickname())
                .userImg(member.getImgUrl())
                .postStatus(chatRoom.getDeliveryPost().getPostStatus())
                .school(chatRoom.getDeliveryPost().getSchool())
                .building(chatRoom.getDeliveryPost().getBuilding())
                .sanitarySize(chatRoom.getDeliveryPost().getSanitarySize())
                .sanitaryNum(chatRoom.getDeliveryPost().getPostNumber())
                .createdAt(chatRoom.getJoinedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .lastMessage(chatRoom.getLastMessage())
                .isWriter(isWriter)
                .build();
    }
}
