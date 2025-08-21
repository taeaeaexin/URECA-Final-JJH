package com.ureca.ocean.jjh.mission.dto;

import com.ureca.ocean.jjh.mission.entity.Mission;
import com.ureca.ocean.jjh.mission.entity.MissionCondition;
import com.ureca.ocean.jjh.mission.entity.UserMission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MyMissionDto {
    private UUID missionId;
    private String name;
    private String description;
    private int expReward;

    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    private UUID missionConditionId;
    private String requireType;
    private int requireValue;
    private int myValue;

    // MyMissions → MyMissionsDto 변환용 정적 메서드
    public static MyMissionDto from(Mission mission, UserMission userMission, MissionCondition missionCondition, int myValue) {
        return MyMissionDto.builder()
                .missionId(userMission.getMission().getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .expReward(mission.getExpReward())
                .completed(userMission.isCompleted())
                .completedAt(userMission.getCompletedAt())
                .createdAt(userMission.getCreatedAt())
                .missionConditionId(missionCondition != null ? missionCondition.getId() : null)
                .requireType(missionCondition != null ? missionCondition.getRequireType() : null)
                .requireValue(missionCondition != null ? missionCondition.getRequireValue() : 1)
                .myValue(myValue)
                .build();
    }
}
