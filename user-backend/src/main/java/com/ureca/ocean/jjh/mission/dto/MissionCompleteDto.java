package com.ureca.ocean.jjh.mission.dto;

import com.ureca.ocean.jjh.mission.entity.UserMission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MissionCompleteDto {
    private String nickname;
    private String missionName;
    private LocalDateTime completedAt;
    private int expReward;

    // MissionComplete → MissionCompleteDto 변환용 정적 메서드
    public static MissionCompleteDto from(UserMission userMission) {
        return MissionCompleteDto.builder()
                .nickname(userMission.getUser().getNickname())
                .missionName(userMission.getMission().getName())
                .completedAt(userMission.getCompletedAt())
                .expReward(userMission.getMission().getExpReward())
                .build();
    }

}
