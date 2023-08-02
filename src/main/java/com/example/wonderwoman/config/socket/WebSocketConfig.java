package com.example.wonderwoman.config.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    //메시지를 중간에서 라우팅할 때 사용하는 메시지 브로커를 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //해당 주소(prefix) 구독 클라이언트에게 메시지 보냄 -> 1:1
        registry.enableSimpleBroker("/sub");
        //도착 경로에 대한 prefix -> /sub에 구독을 신청했을 때 실제 경로가 /chat/app/sub임
        registry.setApplicationDestinationPrefixes("/app/");
    }

    //클라이언트에서 연결할 websocket endpoint를 지정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //웹소켓이 handshake를 하기 위해 연결하는 endpoint
        registry.addEndpoint("/chatting")
                .setAllowedOriginPatterns("*")  //추후 변경
                .withSockJS(); //만약 Websocket 지원하지 않는 브라우저라면 polling 방식 수행하도록 함 -> 대신 /webSocket 붙여줘야 함
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
        channelRegistration.interceptors(stompHandler);
    }
}
