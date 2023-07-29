package com.example.wonderwoman.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //메시지를 중간에서 라우팅할 때 사용하는 메시지 브로커를 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //해당 주소(prefix) 구독 클라이언트에게 메시지 보냄 -> 1:1
        registry.enableSimpleBroker("/chat/sub");
        //도착 경로에 대한 prefix -> /sub에 구독을 신청했을 때 실제 경로가 /chat/app/sub임
        registry.setApplicationDestinationPrefixes("/chat/app");
    }

    //클라이언트에서 연결할 websocket endpoint를 지정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //웹소켓이 handshake를 하기 위해 연결하는 endpoint
        registry.addEndpoint("/chat/chatting")
                .setAllowedOriginPatterns("*://*")
                .withSockJS();
    }
}
