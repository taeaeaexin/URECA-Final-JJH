package com.ureca.ocean.jjh.direction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "길찾기 응답 DTO")
@Document
public class PathFindResponseDto {

    @Id
    private String id; // 또는 ObjectId id;

    @Schema(description = "트랜잭션 ID", example = "2023072812345678")
    private String trans_id;

    @Schema(description = "경로 리스트")
    private List<Route> routes;

    private UUID userId;
    private boolean bookmark;
    private String scenario;
    @Data
    @Schema(description = "경로 정보")
    public static class Route {
        @Schema(description = "결과 코드", example = "0")
        private int result_code;

        @Schema(description = "결과 메시지", example = "성공")
        private String result_msg;

        private Summary summary;
        private List<Section> sections;
    }

    @Data
    @Schema(description = "요약 정보")
    public static class Summary {
        private Point origin;
        private Point destination;
        private List<Waypoint> waypoints;

        @Schema(description = "우선순위", example = "FASTEST")
        private String priority;

        private Bound bound;
        private Fare fare;

        @Schema(description = "총 거리 (m)", example = "12345")
        private int distance;

        @Schema(description = "총 소요 시간 (ms)", example = "153000")
        private int duration;
    }

    @Data
    @Schema(description = "좌표 정보")
    public static class Point {
        @Schema(description = "이름", example = "출발지")
        private String name;

        @Schema(description = "경도", example = "127.1086228")
        private double x;

        @Schema(description = "위도", example = "37.4012191")
        private double y;
    }

    @Data
    @Schema(description = "경유지 정보")
    public static class Waypoint {
        private String name;
        private String recommendReason;
        private double x;
        private double y;
    }

    @Data
    @Schema(description = "경로 바운딩 박스")
    public static class Bound {
        private double min_x;
        private double min_y;
        private double max_x;
        private double max_y;
    }

    @Data
    @Schema(description = "요금 정보")
    public static class Fare {
        private int taxi;
        private int toll;
    }

    @Data
    @Schema(description = "구간 정보")
    public static class Section {
        private int distance;
        private int duration;
        private Bound bound;
        private List<Road> roads;
        private List<Guide> guides;
    }

    @Data
    @Schema(description = "도로 정보")
    public static class Road {
        private String name;
        private int distance;
        private int duration;
        private double traffic_speed;
        private int traffic_state;
        private List<Double> vertexes;
    }

    @Data
    @Schema(description = "안내 정보")
    public static class Guide {
        private String name;
        private double x;
        private double y;
        private int distance;
        private int duration;
        private int type;
        private String guidance;
        private int road_index;
    }
}
