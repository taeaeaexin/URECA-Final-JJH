package com.ureca.ocean.jjh.direction.client;

import com.ureca.ocean.jjh.direction.dto.request.PathFindRequestDto;
import com.ureca.ocean.jjh.direction.dto.response.PathFindResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@RequiredArgsConstructor
@Component
@Slf4j
public class KakaoMobilityClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY;

    public PathFindResponseDto getDirections(PathFindRequestDto requestDto) {
        String url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", KAKAO_API_KEY);

        // 요청 본문 설정
        HttpEntity<PathFindRequestDto> entity = new HttpEntity<>(requestDto, headers);
        log.info("요청 본문 설정 : " + entity.toString());
        // POST 요청
        ResponseEntity<PathFindResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        log.info("응답 결과: {}", response.getBody());
        return response.getBody();
    }
}

