package com.example.wonderwoman.chatting.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomRequest {
    private Long postId;

    //WebSocketSession은 WebSocket Connection이 맺어진 세션
    private Set<WebSocketSession> sessions = new HashSet<>();

}
