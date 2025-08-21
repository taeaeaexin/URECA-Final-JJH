package com.ureca.ocean.jjh.mission.dto;

import com.ureca.ocean.jjh.mission.entity.Mission;
import com.ureca.ocean.jjh.mission.entity.MissionCondition;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MissionWithConditionDto {
    private UUID id;
    private String name;
    private String description;
    private int expReward;

    private UUID missionConditionId;
    private String requireType;
    private int requireValue;

    // Missions → MissionDto 변환용 정적 메서드
    public static MissionWithConditionDto from(Mission mission, MissionCondition missionCondition) {
        return MissionWithConditionDto.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .expReward(mission.getExpReward())
                .missionConditionId(missionCondition.getId())
                .requireType(missionCondition.getRequireType())
                .requireValue(missionCondition.getRequireValue())
                .build();
    }
}
