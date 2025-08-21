//package com.ureca.ocean.jjh.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
///**
// * STOMP를 사용하여 메시지 브로커를 설정합니다
// * WebSocket 메시지 브로커의 설정을 정의하는 메서드들을 제공합니다. 이를 통해 메시지 브로커를 구성하고 STOMP 엔드포인트를 등록할 수 있습니다.
// */
//
//@Configuration                      // 설정 클래스로 지정합니다.
//@EnableWebSocketMessageBroker       // WebSocket 메시지 브로커를 활성화합니다.
//@Slf4j
//public class WebSocketStompBrokerConfig implements WebSocketMessageBrokerConfigurer {
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // 클라이언트가 연결할 엔드포인트 (SockJS fallback 포함)
//        log.info("registerStompEndPoints 진입 ");
//        registry.addEndpoint("/api/chat/ws")
////                .setAllowedOrigins(
////                        "http://127.0.0.1:5500",
////                        "http://localhost:5500",
////                        "http://15.164.81.45"
////                )
////                .setAllowedOriginPatterns("*")
//                .withSockJS();
//        log.info("registerStompEndPoints 탈출 ");
//    }
//
//    //구독하는 endpoint , /topic을 구독한 사용자에게 메시지를 모두 보낸다.
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        log.info("configureMessageBroker 진입 ");
//        // 구독(prefix) 경로 (메시지 브로커)
//        registry.enableSimpleBroker("/topic");
//        log.info("configureMessageBroker 진입 후 구독 경로 (/topic) 설정 ");
//        // 클라이언트가 서버로 보내는 메시지 prefix
//        registry.setApplicationDestinationPrefixes("/app");
//        log.info("configureMessageBroker 탈출 ");
//    }
//}