package com.ureca.ocean.jjh.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RouteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("websocket_route", r -> r
                        .path("/ws/chat")
                        .uri("ws://10.0.2.41:8082")) // WebSocket 서버 주소

//                // HTTP 요청: /api/ws/chat/info/** 는 http 프로토콜로
//                .route("ws_chat_info_route", r -> r.path("/api/ws/chat/info/**")
//                        .uri("http://10.0.2.41:8082"))
//
//                // WebSocket 요청: /api/ws/chat/** 는 ws 프로토콜로
//                // (단, /api/ws/chat/info/** 경로와 겹치지 않도록 주의)
//                .route("ws_chat_ws_route", r -> r
//                        .path("/api/ws/chat/**")
//                        .and()
//                        .predicate(exchange -> !exchange.getRequest().getURI().getPath().startsWith("/api/ws/chat/info"))
//                        .uri("ws://10.0.2.41:8082"))

                // 기존 다른 라우팅들
                .route("auth_route", r -> r.path("/api/auth/**")
                        .uri("http://10.0.1.17:8081"))
                .route("map_route", r -> r.path("/api/map/**")
                        .uri("http://10.0.1.239:8082"))
                .route("ai_route", r -> r.path("/api/ai/**")
                        .uri("http://10.0.1.141:8082"))
                .route("user_route", r -> r.path("/api/user/**")
                        .uri("http://10.0.2.41:8082"))

                .build();
    }
}
