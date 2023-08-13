package com.example.wonderwoman.chatting.entity;

import com.example.wonderwoman.common.entity.BaseTimeEntity;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom extends BaseTimeEntity {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @Column(name = "roomId")
    private String id;

    @Column(length = 1000)
    private String lastMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryPost_id", referencedColumnName = "id")
    private DeliveryPost deliveryPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callerId", referencedColumnName = "id")
    private Member caller;   //도움 요청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helperId", referencedColumnName = "id")
    private Member helper;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Builder
    public ChatRoom(Member caller, Member helper, DeliveryPost deliveryPost) {
        this.id = UUID.randomUUID().toString();
        this.caller = caller;
        this.helper = helper;
        this.deliveryPost = deliveryPost;
    }

    public void updateLastMessage(String lastMessage) {
        if (!Objects.nonNull(lastMessage)) {
            throw new WonderException(ErrorCode.VALUE_IS_NONNULL);
        } else {
            this.lastMessage = lastMessage;
        }
    }

    public void addChatMessage(ChatMessage message) {
        this.chatMessageList.add(message);
    }

}
