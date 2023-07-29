package com.example.wonderwoman.chatting.controller;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.example.wonderwoman.chatting.entity.ChatRoom;
import com.example.wonderwoman.chatting.entity.ListResult;
import com.example.wonderwoman.chatting.request.ChatRoomRequest;
import com.example.wonderwoman.chatting.request.ChatRoomStatusRequest;
import com.example.wonderwoman.chatting.response.ChatRoomInfoResponse;
import com.example.wonderwoman.chatting.response.ChatRoomResponse;
import com.example.wonderwoman.chatting.service.ChatService;
import com.example.wonderwoman.chatting.service.ResponseService;
import com.example.wonderwoman.common.dto.NormalResponseDto;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/chat")
public class ChatRoomController {

    private final ChatService chatService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;


    //사용자별 전체 방 조회(요청, 출동 상관 없음)
    @GetMapping("/rooms")
    public ListResult<ChatRoom> rooms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
        return responseService.getListResult(chatService.getMemberEnterRooms(member));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomResponse> roomChatMessages(@PathVariable String roomId) {
        ListResult<ChatMessage> messages = responseService.getListResult(chatService.chatMessageList(roomId));
        return ResponseEntity.ok(ChatRoomResponse.of(chatService.findRoomById(roomId), messages));
    }

    @PostMapping("/room")
    public ResponseEntity<ChatRoomInfoResponse> createRoom(Long memberId, ChatRoomRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = chatService.createChatRoom(member, request.getPostId());
        chatService.enterChatRoom(chatRoom.getId());
        return ResponseEntity.ok(ChatRoomInfoResponse.of(chatRoom));
    }

    @PostMapping("/room/status")
    public ResponseEntity<ChatRoomResponse> updateRoomStatus(Long memberId, ChatRoomStatusRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new WonderException(ErrorCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = chatService.findRoomById(request.getChatRoomId());
        chatService.updatePostStatus(chatRoom, request.getStatus());
        ListResult<ChatMessage> messages = responseService.getListResult(chatService.chatMessageList(chatRoom.getId()));
        return ResponseEntity.ok(ChatRoomResponse.of(chatRoom, messages));
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<NormalResponseDto> deleteRoom(@PathVariable String roomId) {
        chatService.deleteById(roomId);
        return ResponseEntity.ok(NormalResponseDto.success());
    }
}
