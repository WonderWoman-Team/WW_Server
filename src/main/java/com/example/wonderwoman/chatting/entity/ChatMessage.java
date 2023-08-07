package com.example.wonderwoman.chatting.entity;

import com.example.wonderwoman.common.entity.BaseTimeEntity;
import com.example.wonderwoman.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member sender;  //보낸 이

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(length = 1000)
    private String message; //메시지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Builder
    public ChatMessage(Member sender, ChatRoom chatRoom, String message, String messageType) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.message = message;
        this.type = MessageType.valueOf(messageType);
    }

    public enum MessageType {
        ENTER, EXIT, TALK
    }
}
