package com.backend.gitssum.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker //Stomp 사용을 위해 선언
public class webSocketConfig implements WebSocketMessageBrokerConfigurer {

    // WebSocketMessageBrokerConfigurer 상속받아 아래 두 메서드를 재정의

    @Override // 메시지 브로커에 관련한 설정을 정의
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        // 메시지 구독 요청의 prefix -> /sub로 시작하도록 설정 // 백->프론트
        config.setApplicationDestinationPrefixes("/pub");
        //메시지 발행 요청의 prefix -> /pub 로 시작하도록 설정 // 프론트 -->백
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("http://localhost:3000")
                .setAllowedOrigins("https://main.d20iwpsyv6d6f7.amplifyapp.com")
                .withSockJS();
      // SockJs Fallback 을 활용하여 Stomp 엔드포인트 설정
        // 메시지 발행하는 prefix /pub 로 시작 하도록 설정
        // 구독 요청의 prefix /sub 으로 시작 하도록 설정
        // 현재 엔드포인트 -> /webSocket
    }
}
