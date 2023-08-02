package com.example.wonderwoman.chatting.service;

import com.example.wonderwoman.chatting.entity.ChatMessage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate<String, Object> chatRedisTemplate;

    public void publish(ChannelTopic topic, ChatMessage message) {
        chatRedisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
