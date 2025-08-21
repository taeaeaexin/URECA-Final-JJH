package com.ureca.ocean.jjh.jwt;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationWebFilter implements WebFilter {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		log.info("----------------------------------"+ path + "----------------------------------");
		if (path.startsWith("/api/ws/chat/info")) {
			log.info(path + "ws/info 요청!");
			return chain.filter(exchange); // 인증 필터 동작 안 함
		}
		if (path.startsWith("/api/ws/chat")) {
			log.info(path + "/api/we/chat 요청!");
			return chain.filter(exchange); // 인증 필터 동작 안 함
		}
		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		log.info("jwt filter 내의 token : " + token);
		if (token != null && jwtUtil.validateToken(token)) {
			String email = jwtUtil.getEmailFromToken(token); //username의 실제 값은 auth-backend에서 email을 넣어서 보내줌.

			Authentication auth = new UsernamePasswordAuthenticationToken(email, null, List.of());
			SecurityContext context = new SecurityContextImpl(auth);


			//userId를 microservice로 넘기기 전
//			return chain.filter(exchange)
//					.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

			//userId를 microservice로 넘기기 후
			String encodedUsername = URLEncoder.encode(email, StandardCharsets.UTF_8);
			ServerHttpRequest mutatedRequest = exchange.getRequest()
					.mutate()
					.header("X-User-email", encodedUsername)
					.build();

			ServerWebExchange mutatedExchange = exchange.mutate()
					.request(mutatedRequest)
					.build();
			log.info("username header에 추가 : " + email);
			return chain.filter(mutatedExchange)
					.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));

		}

		return chain.filter(exchange);
	}
}
