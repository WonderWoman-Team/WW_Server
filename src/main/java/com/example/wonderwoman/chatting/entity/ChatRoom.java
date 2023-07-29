package com.example.wonderwoman.chatting.entity;

import com.example.wonderwoman.common.entity.BaseTimeEntity;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom extends BaseTimeEntity implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private String id;

    @Column
    private String lastMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryPost_id", referencedColumnName = "id")
    private DeliveryPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callerId", referencedColumnName = "id")
    private Member caller;   //도움 요청자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helperId", referencedColumnName = "id")
    private Member helper;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Builder
    public ChatRoom(Member caller, Member helper, DeliveryPost deliveryPost) {
        this.id = UUID.randomUUID().toString();
        this.caller = caller;
        this.helper = helper;
        this.post = deliveryPost;
    }

    public void updateLastMessage(String lastMessage) {
        if (Objects.nonNull(lastMessage)) {
            throw new WonderException(ErrorCode.VALUE_IS_NONNULL);
        } else
            this.lastMessage = lastMessage;
    }

    public void addChatMessage(ChatMessage message) {
        this.chatMessageList.add(message);
    }


}
