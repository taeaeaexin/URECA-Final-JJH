package com.ureca.ocean.jjh.direction.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.ocean.jjh.direction.client.KakaoMobilityClient;
import com.ureca.ocean.jjh.direction.dto.ScenarioResponse;
import com.ureca.ocean.jjh.direction.dto.request.PathFindRequestDto;

import com.ureca.ocean.jjh.direction.dto.response.PathFindResponseDto;
import com.ureca.ocean.jjh.direction.repository.DirectionHistoryRepository;
import com.ureca.ocean.jjh.direction.repository.PathFindRepository;
import com.ureca.ocean.jjh.direction.service.DirectionService;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.mcp.client.McpClient;
import com.ureca.ocean.jjh.mcp.dto.McpRequestDto;
import com.ureca.ocean.jjh.mcp.dto.McpResponseDto;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DirectionServiceImpl implements DirectionService {
    private final PathFindRepository pathFindRepository;
    private final DirectionHistoryRepository directionRepository;
    private final KakaoMobilityClient kakaoMobilityClient;
    private final UserClient userClient;
    private final McpClient mcpClient;
    private String wayPointFindPromptsHead;
    private String wayPointFindPromptsBody;
    private String wayPointFindPromptsBody2;
    private String wayPointFindPromptsBody3;
    @PostConstruct
    public void loadPrompts(){
        try{
            Resource systemResource1= new ClassPathResource("prompts/wayPointFindPromptsHead.txt");
            Resource systemResource2= new ClassPathResource("prompts/wayPointFindPromptsBody.txt");
            Resource systemResource3= new ClassPathResource("prompts/wayPointFindPromptsBody2.txt");
            Resource systemResource4= new ClassPathResource("prompts/wayPointFindPromptsBody3.txt");

            try (InputStream in = systemResource1.getInputStream()) {
                this.wayPointFindPromptsHead = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            try (InputStream in = systemResource2.getInputStream()) {
                this.wayPointFindPromptsBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            try (InputStream in = systemResource3.getInputStream()) {
                this.wayPointFindPromptsBody2 = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            try (InputStream in = systemResource4.getInputStream()) {
                this.wayPointFindPromptsBody3 = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }catch (IOException e) {
            throw new UncheckedIOException("프롬프트 로드 중 오류 발생", e);
        }
    }
    @Override
    public PathFindResponseDto getPath(String email,PathFindRequestDto requestDto) {
        PathFindResponseDto pathFindResponseDto = kakaoMobilityClient.getDirections(requestDto);

        if(email != null){
            UserDto userDto = userClient.getUserByEmail(email);
            pathFindResponseDto.setUserId(userDto.getId());
        }

        pathFindResponseDto.getRoutes().get(0).getSummary().getOrigin().setName(requestDto.getOrigin().getName());
        pathFindResponseDto.getRoutes().get(0).getSummary().getDestination().setName(requestDto.getDestination().getName());

        pathFindResponseDto.setBookmark(false);

        log.info("kakao api 호출 후 응답 받기 완료");
        pathFindRepository.save(pathFindResponseDto);
        log.info("mongodb에 path find repository 저장 완료");
        return pathFindResponseDto;
    }
    @Override
    public PathFindResponseDto getPathByLLM(String email, PathFindRequestDto pathFindRequestDto) {
        UserDto userDto = userClient.getUserByEmail(email);
        String origin = pathFindRequestDto.getOrigin().toString();
        String destination = pathFindRequestDto.getDestination().toString();

        // 중심 좌표
        double centerLat = Double.parseDouble(pathFindRequestDto.getOrigin().getY()); // 위도
        double centerLng = Double.parseDouble(pathFindRequestDto.getOrigin().getX()); // 경도

        // 10km 반경 범위 계산
        double distanceKm = 2.5;
        double latDelta = distanceKm / 111.0; // 위도는 1도 ≈ 111km

        double radLat = Math.toRadians(centerLat); // 위도를 라디안으로 변환
        double lngDelta = distanceKm / (111.0 * Math.cos(radLat)); // 경도는 위도에 따라 보정 필요

        double latMin = centerLat - latDelta;
        double latMax = centerLat + latDelta;
        double lngMin = centerLng - lngDelta;
        double lngMax = centerLng + lngDelta;

        // 디버깅 로그 (선택)
        log.info("🔍 Bounding Box 계산됨: latMin=" + latMin + ", latMax=" + latMax + ", lngMin=" + lngMin + ", lngMax=" + lngMax);

//        SELECT *
//          FROM stores
//         WHERE latitude BETWEEN :latMin AND :latMax
//           AND longitude BETWEEN :lngMin AND :lngMax;

        McpResponseDto mcpResponseDto = mcpClient.queryLLM(new McpRequestDto(
                wayPointFindPromptsHead + userDto.getId().toString() +wayPointFindPromptsBody2 + String.valueOf(latMin) + "AND" + String.valueOf(latMax) + " AND longitude BETWEEN "+String.valueOf(lngMin) +  " AND "+ String.valueOf(lngMax)+";"+wayPointFindPromptsBody3 + origin +"에서" + destination +wayPointFindPromptsBody
        ));
        log.info("mcp 답변 : " + mcpResponseDto.getResult());
        ObjectMapper objectMapper = new ObjectMapper();
        ScenarioResponse scenarioResponse = new ScenarioResponse();
        try{
            scenarioResponse = objectMapper.readValue(mcpResponseDto.getResult(), ScenarioResponse.class);
        }catch(Exception e){
            throw(new MapException(ErrorCode.PARSING_MCP_RESPONSE_ERROR));
        }
        List<PathFindRequestDto.Waypoint> wayPoints = new ArrayList<>();
        for (ScenarioResponse.Waypoint wp : scenarioResponse.getWaypoints()) {
            System.out.println("📍 " + wp.getName() + " - " + wp.getRecommendReason());
            System.out.println("    위도(y): " + wp.getY() + ", 경도(x): " + wp.getX());
            PathFindRequestDto.Waypoint wayPoint = new PathFindRequestDto.Waypoint(wp.getName(),wp.getX(),wp.getY());
            wayPoints.add(wayPoint);
        }
        pathFindRequestDto.setWaypoints(wayPoints);
        PathFindResponseDto pathFindResponseDto = kakaoMobilityClient.getDirections(pathFindRequestDto);

        pathFindResponseDto.setScenario(scenarioResponse.getScenario());
        for(int i = 0 ; i < pathFindResponseDto.getRoutes().get(0).getSummary().getWaypoints().size(); i++){
            PathFindResponseDto.Waypoint wayPoint = pathFindResponseDto.getRoutes().get(0).getSummary().getWaypoints().get(i);
            wayPoint.setRecommendReason(scenarioResponse.getWaypoints().get(i).getRecommendReason());
        }
        pathFindResponseDto.setUserId(userDto.getId());
        pathFindResponseDto.setBookmark(false);
        log.info("kakao api 호출 후 응답 받기 완료");
        pathFindRepository.save(pathFindResponseDto);
        log.info("mongodb에 path find repository 저장 완료");

        return pathFindResponseDto;
    }

    @Override
    public List<PathFindResponseDto> getMyDirectionHistory(String email) {
        UserDto userDto = userClient.getUserByEmail(email);
        List<PathFindResponseDto> list = new ArrayList<>(
                pathFindRepository.findByUserId(userDto.getId())
                        .stream()
                        .toList()
        );

        Collections.reverse(list);  // 이제 안전하게 reverse 가능
        return list;
    }


    @Override
    public List<PathFindResponseDto> getAllBookmarkedDirections(String email) {
        UserDto userDto = userClient.getUserByEmail(email);

        return pathFindRepository.findByBookmarkAndUserId(true, userDto.getId()).stream()
                .toList();
    }

    @Override
    public PathFindResponseDto getDirectionById(String id) {
        return pathFindRepository.findById(id).orElseThrow(() ->  new MapException(ErrorCode.NOT_FOUND_ERROR));
    }

    @Override
    public PathFindResponseDto updateBookmark(String id, boolean bookMark) {
        PathFindResponseDto pathFindResponseDto = pathFindRepository.findById(id)
                .orElseThrow(() -> new MapException(ErrorCode.NOT_FOUND_ERROR));

        pathFindResponseDto.setBookmark(bookMark);


        return pathFindRepository.save(pathFindResponseDto);
    }


    @Override
    public void deleteDirection(String id) {
        pathFindRepository.deleteById(id);
    }
}
