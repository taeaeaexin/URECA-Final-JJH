package com.ureca.ocean.jjh.mission.service.impl;

import com.ureca.ocean.jjh.client.MapClient;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.map.dto.StoreUsageDto;
import com.ureca.ocean.jjh.mission.dto.MissionCompleteDto;
import com.ureca.ocean.jjh.mission.dto.MissionWithConditionDto;
import com.ureca.ocean.jjh.mission.dto.MyMissionDto;
import com.ureca.ocean.jjh.mission.entity.Mission;
import com.ureca.ocean.jjh.mission.entity.MissionCondition;
import com.ureca.ocean.jjh.mission.entity.UserMission;
import com.ureca.ocean.jjh.mission.repository.MissionConditionRepository;
import com.ureca.ocean.jjh.mission.repository.MissionRepository;
import com.ureca.ocean.jjh.mission.repository.UserMissionRepository;
import com.ureca.ocean.jjh.mission.service.MissionService;
import com.ureca.ocean.jjh.user.entity.Attendance;
import com.ureca.ocean.jjh.user.repository.AttendanceRepository;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionConditionRepository missionConditionRepository;
    private final UserMissionRepository userMissionRepository;
    private final AttendanceRepository attendanceRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final MapClient mapClient;

    @Override
    public List<MissionWithConditionDto> getAllMissions() {
        return missionRepository.findAllWithConditions();
    }

    @Override
    public List<MyMissionDto> getMyMissions(String email, Boolean completed) {
        UUID userId = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER))
            .getId();

        List<UserMission> userMissions = (completed == null)
            ? missionRepository.findUserMissionsByUserId(userId)
            : missionRepository.findUserMissionsByUserIdAndCompleted(userId, completed);

        // 출석 여부 확인
        List<Attendance> todayAttendance = attendanceRepository.findByUserIdAndDate(userId, LocalDate.now());

        // 사용 내역 조회 (브랜드 방문 수 계산용)
        List<StoreUsageDto> storeUsage = mapClient.getStoreUsageEmail(email);

        return userMissions.stream()
                .map(um -> {
                    Mission mission = um.getMission();
                    MissionCondition condition = mission.getConditions().stream().findFirst().orElse(null);

                    int myValue = 0;
                    String desc = mission.getDescription();

                    if ("출석체크".equals(desc)) {
                        myValue = !todayAttendance.isEmpty() ? 1 : 0;
                    } else if (condition != null) {
                        myValue = (int) storeUsage.stream()
                                .filter(su -> desc.equals(su.getBrandName()))
                                .count();
                    }

                    return MyMissionDto.from(mission, um, condition, myValue);
                })
                .toList();
    }

    @Override
    @Transactional
    public MissionCompleteDto getMissionComplete(String email, UUID missionId) {
        // user mission 가져오기
        UUID userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER))
                .getId();

        UserMission userMission = userMissionRepository.getUserMissionsByUserId(userId).stream()
                .filter(um -> um.getMission().getId().equals(missionId))
                .findFirst()
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_MISSION));

        // 완료된 미션이면 exception
        if (userMission.isCompleted()) {
            throw new UserException(ErrorCode.ALREADY_COMPLETED);
        }

        // 출석체크 미션
        if(Objects.equals(userMission.getMission().getDescription(), "출석체크")){
            List<Attendance> attendance = attendanceRepository.findByUserIdAndDate(userId, LocalDate.now());
            if(!attendance.isEmpty()) {
                userMission.setCompleted(true);
                userMission.setCompletedAt(LocalDateTime.now());
                return MissionCompleteDto.from(userMission);
            }else{
                throw new UserException(ErrorCode.ATTENDANCE_REQUIRE);
            }
        }

        // 방문 미션
        String brand = userMission.getMission().getDescription();
        List<StoreUsageDto> storeUsage = mapClient.getStoreUsageEmail(email);

        MissionCondition condition = userMission.getMission().getConditions().stream()
                .findFirst()
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_MISSION));

        long count = storeUsage.stream()
                .filter(su -> brand.equals(su.getBrandName()))
                .count();

        if (count >= condition.getRequireValue()) {
            userMission.setCompleted(true);
            userMission.setCompletedAt(LocalDateTime.now());
            return MissionCompleteDto.from(userMission);
        } else {
            throw new UserException(ErrorCode.MISSION_NOT_COMPLETED);
        }
    }
}
