package com.ureca.ocean.jjh.mission.repository;

import com.ureca.ocean.jjh.mission.entity.MissionCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MissionConditionRepository extends JpaRepository<MissionCondition, UUID> {
    List<MissionCondition> findAllByMissionIdIn(List<UUID> missionId);
}
