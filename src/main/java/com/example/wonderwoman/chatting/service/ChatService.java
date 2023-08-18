package com.example.wonderwoman.chatting.service;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.repository.ChatMessageRepository;
import com.example.wonderwoman.chatting.repository.ChatRoomRepository;
import com.example.wonderwoman.chatting.response.ChatMessageDto;
import com.example.wonderwoman.chatting.response.ChatRoomInfoResponse;
import com.example.wonderwoman.chatting.response.ChatRoomListDto;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.PostStatus;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.delivery.repository.DeliveryPostRepository;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ChatService {
    public static final String ENTER_INFO = "ENTER_INFO";
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final ObjectMapper objectMapper;
    //채팅방에 발행되는 메시지 처리하는 listener
//    private final RedisMessageListenerContainer redisMessageListenerContainer;
    //구독처리 서비스
//    private final RedisSubscriber redisSubscriber;
//    @Resource(name = "chatRedisTemplate")
//    private final RedisTemplate<String, Object> chatRedisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DeliveryPostRepository deliveryPostRepository;
    private final MemberRepository memberRepository;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    //채팅방 메시지를 발행하기 위한 redis topic 정보.
    //서버 별로 채팅방에 매칭되는 topic 정보를 map에 넣어 roomId로 찾을 수 있도록 함
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
//        opsHashChatRoom = chatRedisTemplate.opsForHash();
//
//        topics = new HashMap<>();
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 리스트 조회
    public List<ChatRoomListDto> findAllRoom(Member member) {
        return chatRoomRepository.findChatRoomsByMember(member.getId())
                .stream()
                .sorted(Comparator.comparing(ChatRoom::getUpdatedAt))
                .map(o -> {
                    if (o.getCaller().getId() == member.getId()) {
                        if (o.getDeliveryPost().isWrittenPost(member))
                            return ChatRoomListDto.of(o, o.getHelper(), true);
                        return ChatRoomListDto.of(o, o.getHelper(), false);
                    } else {
                        if (o.getDeliveryPost().isWrittenPost(member))
                            return ChatRoomListDto.of(o, o.getCaller(), true);
                        return ChatRoomListDto.of(o, o.getCaller(), false);
                    }
                })
                .collect(Collectors.toList());
    }


    public ChatRoomInfoResponse findRoomById(Member member, String id) {
        ChatRoom chatRoom = (ChatRoom) chatRoomRepository.findById(id).orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));
        boolean isWriter = chatRoom.getDeliveryPost().isWrittenPost(member);

        if (chatRoom.getHelper().getId() == member.getId()) {    //사용자가 출동자면 요청자 정보만 넣어서
            return ChatRoomInfoResponse.of(chatRoom, chatRoom.getCaller(), isWriter);

        } else if (chatRoom.getCaller().getId() == member.getId()) { //사용자가 요청자면 출동자 정보만 넣어서
            return ChatRoomInfoResponse.of(chatRoom, chatRoom.getHelper(), isWriter);
        } else {
            throw new WonderException(ErrorCode.FORBIDDEN_CHATROOM);
        }
    }

    //채팅방 생성
    public ChatRoomInfoResponse createChatRoom(Member member, Long postId) {
        DeliveryPost post = deliveryPostRepository.findById(postId)
                .orElseThrow(() -> new WonderException(ErrorCode.ARTICLE_NOT_FOUND));

        ChatRoom chatRoom;

        Member user;
        //요청자, 출동자에 따라 다르게 저장
        if (post.getPostReqType().equals(ReqType.REQUEST)) {
            chatRoom = ChatRoom.builder()
                    .caller(post.getMember())
                    .helper(member)
                    .deliveryPost(post)
                    .build();
            user = chatRoom.getCaller();

        } else {
            chatRoom = ChatRoom.builder()
                    .caller(member)
                    .helper(post.getMember())
                    .deliveryPost(post)
                    .build();
            user = chatRoom.getHelper();
        }

//        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getId(), chatRoom);
        chatRooms.put(chatRoom.getId(), chatRoom);
        chatRoomRepository.save(chatRoom);

        if (post.getPostStatus().equals(PostStatus.NONE))
            post.updatePostStatus(PostStatus.CHATTING);

        return ChatRoomInfoResponse.of(chatRoom, user, false);
    }

    //채팅방 입장
//    public void enterChatRoom(String roomId) {
//        ChannelTopic topic = topics.get(roomId);
//        if (topic == null)
//            topic = new ChannelTopic(roomId);
//        redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
//        topics.put(roomId, topic);
//    }

    //채팅메시지 저장
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

//    public ChannelTopic getTopic(String roomId) {
//        return topics.get(roomId);
//    }

//    public ChannelTopic getTopic(String roomId) {
//        return topics.get(roomId);
//    }

//    public List<ChatRoom> getCallerEnterRooms(Member caller) {
//        return chatRoomRepository.findChatRoomsByCaller(caller);
//    }
//
//    public List<ChatRoom> getHelperEnterRooms(Member helper) {
//        return chatRoomRepository.findChatRoomsByHelper(helper);
//    }
//
//    public List<ChatRoom> getMemberEnterRooms(Member member) {
//        return chatRoomRepository.findChatRoomsByMember(member.getId());
//    }

    //딜리버리 상태 변경
    @Transactional
    public void updatePostStatus(String chatRoomId, PostStatus postStatus) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));
        DeliveryPost deliveryPost = chatRoom.getDeliveryPost();
        deliveryPost.updatePostStatus(postStatus);
        deliveryPostRepository.save(deliveryPost);
    }

    //채팅방 삭제(퇴장)
    @Transactional
    public void deleteById(Member member, String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));

        if (chatRoom.getHelper().getId() == member.getId() || chatRoom.getCaller().getId() == member.getId()) {
            chatRoomRepository.deleteById(chatRoomId);
        } else {
            throw new WonderException(ErrorCode.FORBIDDEN_CHATROOM);
        }
    }

    //채팅방 메시지 리스트
    public List<ChatMessageDto> chatMessageList(Member member, String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new WonderException(ErrorCode.CHATROOM_NOT_FOUND));

        if (chatRoom.getCaller().getId() == member.getId() || chatRoom.getHelper().getId() == member.getId()) {
            log.info("레포지토리에서 메시지 내역을 가져옵니다.");
            List<ChatMessageDto> dtos = chatMessageRepository.getChatMessagesByChatRoom(chatRoom)
                    .stream()
                    .map(o -> ChatMessageDto.of(o))
                    .collect(Collectors.toList());

            log.info(dtos.size() + "만큼 있습니다.");
            Collections.sort(dtos, (o1, o2) -> {
                return o2.getSendTime().compareTo(o1.getSendTime());
            });

            return dtos;
        } else {
            throw new WonderException(ErrorCode.FORBIDDEN_CHATROOM);
        }
    }
}
