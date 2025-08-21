package com.ureca.ocean.jjh.mission.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ureca.ocean.jjh.mission.entity.Mission;
import com.ureca.ocean.jjh.mission.entity.UserMission;
import com.ureca.ocean.jjh.mission.repository.MissionRepository;
import com.ureca.ocean.jjh.mission.repository.UserMissionRepository;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DailyMissionScheduler {

    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;

    // UTC 기준 오후 3시 → 한국 시간 자정에 실행됨
    @Scheduled(cron = "0 0 15 * * *")
    @Transactional
    public void refreshDailyMissions() {
        // 1. 기존 미션 삭제
        userMissionRepository.deleteAll();

        // 2. 오늘 미션 생성
        List<Mission> allMissions = missionRepository.findAll();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Mission> missions = pickRandomMissions(allMissions, 3);

            for (Mission mission : missions) {
                UserMission userMission = new UserMission();
                userMission.setUser(user);
                userMission.setMission(mission);
                userMission.setCompleted(false);
                userMission.setCreatedAt(LocalDateTime.now());
                userMissionRepository.save(userMission);
            }
        }
    }


    private List<Mission> pickRandomMissions(List<Mission> all, int count) {
        if (count < 1) return List.of();

        // 1. 출석체크 미션 추출
        Mission attendanceMission = all.stream()
                .filter(m -> "출석체크".equals(m.getDescription()))
                .findFirst()
                .orElse(null);

        // 2. 출석체크 제외 미션
        List<Mission> others = all.stream()
                .filter(m -> !"출석체크".equals(m.getDescription()))
                .collect(Collectors.toCollection(ArrayList::new));

        // 3. description 기준으로 그룹화
        Map<String, List<Mission>> groupedByDesc = others.stream()
                .collect(Collectors.groupingBy(Mission::getDescription));

        // 4. 각 그룹에서 하나씩 랜덤 선택
        List<Mission> selected = new ArrayList<>();
        for (List<Mission> missions : groupedByDesc.values()) {
            Collections.shuffle(missions);
            selected.add(missions.get(0)); // 각 그룹에서 하나만 뽑음
        }

        // 5. 랜덤 순서로 섞고 count만큼 잘라서 선택
        Collections.shuffle(selected);
        if (selected.size() > count - 1) {
            selected = selected.subList(0, count - 1);
        }

        // 6. 출석체크 미션이 있다면 맨 앞에 추가
        if (attendanceMission != null) {
            selected.add(0, attendanceMission);
        }

        return selected;
    }

}

