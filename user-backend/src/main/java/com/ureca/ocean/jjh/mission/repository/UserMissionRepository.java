package com.ureca.ocean.jjh.mission.repository;

import com.ureca.ocean.jjh.mission.entity.Mission;
import com.ureca.ocean.jjh.mission.entity.UserMission;
import com.ureca.ocean.jjh.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, UUID> {
    List<UserMission> getUserMissionsByUserId(UUID userId);
    boolean existsByUserAndMission(User user, Mission mission);

}
