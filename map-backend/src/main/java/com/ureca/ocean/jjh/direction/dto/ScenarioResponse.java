package com.ureca.ocean.jjh.direction.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ScenarioResponse {
    private String scenario;
    private List<Waypoint> waypoints;

    @Data
    public static class Waypoint {
        private String name;
        private String recommendReason;
        private double x;
        private double y;
    }
}
