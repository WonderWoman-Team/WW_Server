package com.example.wonderwoman.chatting.service;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.repository.ChatMessageRepository;
import com.example.wonderwoman.chatting.repository.ChatRoomRepository;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.PostStatus;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {
    public static final String ENTER_INFO = "ENTER_INFO";
    private static final String CHAT_ROOMS = "CHAT_ROOM";

    //채팅방에 발행되는 메시지 처리하는 listener
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    //구독처리 서비스
    private final RedisSubscriber redisSubscriber;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DeliveryPostRepository deliveryPostRepository;
    private final MemberRepository memberRepository;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    //채팅방 메시지를 발행하기 위한 redis topic 정보.
    //서버 별로 채팅방에 매칭되는 topic 정보를 map에 넣어 roomId로 찾을 수 있도록 함
    private Map<String, ChannelTopic> topics;
    private HashOperations<String, String, String> hashOpsEnterInfo;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();

        topics = new HashMap<>();
    }

    public ChatRoom findRoomById(String id) {
        ChatRoom chatRoom = (ChatRoom) chatRoomRepository.findById(id).orElseThrow();
        return chatRoom;
    }

    public ChatRoom createChatRoom(Member member, Long postId) {
        DeliveryPost post = deliveryPostRepository.findById(postId)
                .orElseThrow(() -> new WonderException(ErrorCode.ARTICLE_NOT_FOUND));

        ChatRoom chatRoom;

        //요청자, 출동자에 따라 다르게 저장
        if (post.getPostReqType().equals(ReqType.REQUEST)) {
            chatRoom = ChatRoom.builder()
                    .caller(post.getMember())
                    .helper(member)
                    .deliveryPost(post)
                    .build();
        } else {
            chatRoom = ChatRoom.builder()
                    .caller(member)
                    .helper(post.getMember())
                    .deliveryPost(post)
                    .build();
        }

        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getId(), chatRoom);
        chatRoomRepository.save(chatRoom);

        if (post.getPostStatus().equals(PostStatus.NONE))
            post.setPostStatus(PostStatus.CHATTING);
        return chatRoom;
    }

    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

    public List<ChatRoom> getCallerEnterRooms(Member caller) {
        return chatRoomRepository.findChatRoomsByCaller(caller);
    }

    public List<ChatRoom> getHelperEnterRooms(Member helper) {
        return chatRoomRepository.findChatRoomsByHelper(helper);
    }

    public List<ChatRoom> getMemberEnterRooms(Member member) {
        return chatRoomRepository.findChatRoomsByMember(member.getId());
    }

    public void updatePostStatus(ChatRoom chatRoom, PostStatus postStatus) {
        DeliveryPost deliveryPost = chatRoom.getPost();
        deliveryPost.setPostStatus(postStatus);
        deliveryPostRepository.save(deliveryPost);
    }

    public void deleteById(String chatRoom) {
        chatRoomRepository.deleteById(chatRoom);
    }

    public List<ChatMessage> chatMessageList(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));

        return chatMessageRepository.getChatMessagesByChatRoom(chatRoom);
    }

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getMemberEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeMemberEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }
}
