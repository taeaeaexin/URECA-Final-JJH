package com.ureca.ocean.jjh.config;

import com.ureca.ocean.jjh.jwt.JwtAuthenticationWebFilter;
import com.ureca.ocean.jjh.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final MyAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/user/article").permitAll()
                        .pathMatchers(
                                "/",
                                "/swagger-ui/index.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/api-docs/swagger-config",
                                "/api-ui.html",
                                "/webjars/**",
                                "/api/auth/login",
                                "/api/auth/kakao/callback",
                                "/api/auth/kakao/signup",
                                "/api/user/signup",
                                "/api/auth/v3/api-docs",
                                "/api/user/v3/api-docs",
                                "/api/ai/v3/api-docs",
                                "/api/map/v3/api-docs",
                                "/api/map/ocr",
                                "/api/map/brand",
                                "/api/map/store",
                                "/api/map/stores",
                                "/api/map/benefit/*",
                                "/api/map/store/*",
                                "/api/map/usage/*",
                                "/api/map/user/rank",
                                "/api/map/store/rank",
                                "/api/map/direction/path",
                                "/api/map/direction/pathByLLM",
                                "/ws/chat",
                                "/api/user/isDupNickname",
                                "/api/user/isDupEmail",
                                "/api/user/article/locations",
                                "/api/user/article/detail"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(new JwtAuthenticationWebFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
