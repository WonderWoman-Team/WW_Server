package com.example.wonderwoman.chatting.controller;

import com.example.wonderwoman.chatting.entity.ListResult;
import com.example.wonderwoman.chatting.repository.ChatRoomRepository;
import com.example.wonderwoman.chatting.request.ChatRoomRequest;
import com.example.wonderwoman.chatting.request.ChatRoomStatusRequest;
import com.example.wonderwoman.chatting.response.ChatMessageDto;
import com.example.wonderwoman.chatting.response.ChatRoomInfoResponse;
import com.example.wonderwoman.chatting.response.ChatRoomListDto;
import com.example.wonderwoman.chatting.service.ChatService;
import com.example.wonderwoman.chatting.service.ResponseService;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.delivery.service.DeliveryService;
import com.example.wonderwoman.login.CurrentUser;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/delivery")
@Slf4j
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);
    private final ChatService chatService;
    private final DeliveryService deliveryService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    //사용자별 전체 방 조회(요청, 출동 상관 없음) -> 완챝
    @GetMapping("/rooms")
    public ListResult<ChatRoomListDto> rooms(@CurrentUser Member member) {
        return responseService.getListResult(chatService.findAllRoom(member));
    }

    //특정 방 정보 조회
    @GetMapping("/room/info/{roomId}")
    public ResponseEntity<ChatRoomInfoResponse> roomInfo(@CurrentUser Member member, @PathVariable String roomId) {
        return ResponseEntity.ok(chatService.findRoomById(member, roomId));
    }

    //특정 방 메시지 리스트 조회
    @GetMapping("/room/{roomId}")
    public ListResult<ChatMessageDto> roomChatMessages(@CurrentUser Member member, @PathVariable String roomId) {
        return responseService.getListResult(chatService.chatMessageList(member, roomId));
    }


    //채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<ChatRoomInfoResponse> createRoom(@CurrentUser Member member, @RequestBody ChatRoomRequest request) {
        ChatRoomInfoResponse chatRoomInfoResponse = chatService.createChatRoom(member, request.getPostId());

        logger.info("Chat room created by user: {} with room ID: {}", member.getId(), chatRoomInfoResponse.getId());

        return ResponseEntity.ok(chatRoomInfoResponse);
    }

    //딜리버리 상태 변경
    @PostMapping("/room/status")
    public ResponseEntity<ChatRoomInfoResponse> updateRoomStatus(@CurrentUser Member member, @RequestBody ChatRoomStatusRequest request) {
        chatService.updatePostStatus(member, request.getChatRoomId(), request.getStatus());
        return ResponseEntity.ok(chatService.findRoomById(member, request.getChatRoomId()));
    }

    //채팅방 퇴장(삭제)
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<NormalResponseDto> deleteRoom(@CurrentUser Member member, @PathVariable String roomId) {
        chatService.deleteById(member, roomId);
        return ResponseEntity.ok(NormalResponseDto.success());
    }
}
