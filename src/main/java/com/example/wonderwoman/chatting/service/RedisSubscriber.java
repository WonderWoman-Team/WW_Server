package com.example.wonderwoman.chatting.service;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate chatRedisTemplate;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            //redis에서 발행한 데이터를 받아서 deserialize함
            String publishMessage = (String) chatRedisTemplate.getStringSerializer()
                    .deserialize(message.getBody());
            //메시지로 매핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            //구독자에게 메시지 전송
            messageSendingOperations.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoom().getId(), chatMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
