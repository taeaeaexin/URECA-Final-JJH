package com.ureca.ocean.jjh.mission.service;

import com.ureca.ocean.jjh.mission.dto.MissionCompleteDto;
import com.ureca.ocean.jjh.mission.dto.MissionWithConditionDto;
import com.ureca.ocean.jjh.mission.dto.MyMissionDto;

import java.util.List;
import java.util.UUID;

public interface MissionService {
    List<MissionWithConditionDto> getAllMissions();
    List<MyMissionDto> getMyMissions(String email, Boolean completed);
    MissionCompleteDto getMissionComplete(String email, UUID missionId);
}
