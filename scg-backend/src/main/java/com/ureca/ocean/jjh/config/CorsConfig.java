package com.ureca.ocean.jjh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
                "http://127.0.0.1:5173",
                "http://localhost:5173",
                "http://15.164.81.45",
                "http://127.0.0.1:5500",
                "https://127.0.0.1:5173",
                "https://localhost:5173",
                "https://15.164.81.45",
                "https://127.0.0.1:5500",
                "https://jijoonghae.duckdns.org",
                "https://jijonnghae.site/"
        ));

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}

