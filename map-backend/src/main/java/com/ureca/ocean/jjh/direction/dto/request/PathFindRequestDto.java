package com.ureca.ocean.jjh.direction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "길찾기 요청 DTO")
public class PathFindRequestDto {

    @Schema(description = "출발지 좌표 및 각도", required = true)
    private Coordinate origin;

    @Schema(description = "도착지 좌표", required = true)
    private Coordinate destination;

    @Schema(description = "경유지 목록")
    private List<Waypoint> waypoints;

    @Schema(description = "우선순위 (예: RECOMMEND, SHORTEST, FREE)", example = "RECOMMEND")
    private String priority;

    @Schema(description = "차량 연료 타입 (예: GASOLINE, DIESEL, LPG, EV)", example = "GASOLINE")
    private String car_fuel;

    @Schema(description = "하이패스 여부", example = "false")
    private boolean car_hipass;

    @Schema(description = "대체 경로 제공 여부", example = "false")
    private boolean alternatives;

    @Schema(description = "도로 상세정보 포함 여부", example = "false")
    private boolean road_details;

    @Schema(description = "요약 정보 포함 여부", example = "false")
    private boolean summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "좌표 정보")
    public static class Coordinate {
        @Schema(description="출발지 도착지 이름",example="판교 테크노밸리")
        private String name;

        @Schema(description = "X좌표 (경도)", example = "127.11024293202674")
        private String x;

        @Schema(description = "Y좌표 (위도)", example = "37.394348634049784")
        private String y;

        @Schema(description = "방향 각도 (출발지에서만 사용)", example = "270")
        private Integer angle;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "경유지 정보")
    public static class Waypoint {

        @Schema(description = "경유지 이름", example = "잠실역")
        private String name;

        @Schema(description = "경유지 X좌표", example = "127.101")
        private double x;

        @Schema(description = "경유지 Y좌표", example = "37.402")
        private double y;
    }
}
