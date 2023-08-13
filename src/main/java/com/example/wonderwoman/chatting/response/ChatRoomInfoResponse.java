package com.example.wonderwoman.chatting.response;

import com.example.wonderwoman.delivery.entity.Building;
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
import java.util.List;

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

    private List<Building> building;
 
    private SanitarySize sanitarySize;

    private int sanitaryNum;

    private LocalDateTime createdAt;

    private String lastMessage;

    public static ChatRoomInfoResponse of(ChatRoom chatRoom, Member member) {
        return ChatRoomInfoResponse.builder()
                .id(chatRoom.getId())
                .userId(member.getId())
                .userNickName(member.getNickname())
                .userImg(member.getImgUrl())
                .postStatus(chatRoom.getDeliveryPost().getPostStatus())
                .school(chatRoom.getDeliveryPost().getSchool())
                .building(chatRoom.getDeliveryPost().getBuilding())
                .sanitarySize(chatRoom.getDeliveryPost().getSanitarySize())
                .sanitaryNum(chatRoom.getDeliveryPost().getSanitaryNum())
                .createdAt(chatRoom.getJoinedAt())
                .lastMessage(chatRoom.getLastMessage())
                .build();
    }
}
