package com.ureca.ocean.jjh.direction.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "direction_history")
public class PathFind {


    @Id
    private String trans_id;

    // User 엔티티 대신 ID만 저장
    private UUID userId;

    private boolean bookmark;

    private List<Route> routes;

    @Data
    public static class Route {
        @Schema(description = "결과 코드", example = "0")
        private int result_code;

        @Schema(description = "결과 메시지", example = "성공")
        private String result_msg;

        private Summary summary;
        private List<Section> sections;
    }

    @Data
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
    public static class Point {
        @Schema(description = "이름", example = "출발지")
        private String name;

        @Schema(description = "경도", example = "127.1086228")
        private double x;

        @Schema(description = "위도", example = "37.4012191")
        private double y;
    }

    @Data
    public static class Waypoint {
        private String name;
        private double x;
        private double y;
    }

    @Data
    public static class Bound {
        private double min_x;
        private double min_y;
        private double max_x;
        private double max_y;
    }

    @Data
    public static class Fare {
        private int taxi;
        private int toll;
    }

    @Data
    public static class Section {
        private int distance;
        private int duration;
        private Bound bound;
        private List<Road> roads;
        private List<Guide> guides;
    }

    @Data
    public static class Road {
        private String name;
        private int distance;
        private int duration;
        private double traffic_speed;
        private int traffic_state;
        private List<Double> vertexes;
    }

    @Data
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
