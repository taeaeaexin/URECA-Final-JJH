package com.ureca.ocean.jjh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class MyAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, org.springframework.security.core.AuthenticationException ex) {
		log.warn("인증 실패: {}", ex.getMessage());

		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		String body = """
                {
                    "statusCode": 401,
                    "message": "fail",
                    "data": {
                        "result": "login"
                    }
                }
                """;

		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
				.bufferFactory()
				.wrap(bytes)));
	}
}
