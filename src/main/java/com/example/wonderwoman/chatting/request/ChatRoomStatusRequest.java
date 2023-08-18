package com.example.wonderwoman.chatting.request;

import com.example.wonderwoman.delivery.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomStatusRequest {
    private String chatRoomId;
    private String postId;
    private PostStatus status;
}
