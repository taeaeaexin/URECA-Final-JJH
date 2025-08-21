package com.ureca.ocean.jjh.mcp.client;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.common.constant.DomainConstant;
import com.ureca.ocean.jjh.mcp.dto.McpRequestDto;
import com.ureca.ocean.jjh.mcp.dto.McpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class McpClient {

    private final RestTemplate restTemplate;

    public McpResponseDto queryLLM(McpRequestDto mcpRequestDto) {
        String url = DomainConstant.MCP_URL + "api/mcp/queryLLM";
        log.info("MCP 요청 URL: {}", url);

        // HttpHeaders 설정 (필요 시 Content-Type 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성
        HttpEntity<McpRequestDto> requestEntity = new HttpEntity<>(mcpRequestDto, headers);

        // 요청 실행
        ResponseEntity<McpResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        McpResponseDto mcpResponseDto = response.getBody();
        log.info(mcpResponseDto.toString());
        return mcpResponseDto;
    }
}
