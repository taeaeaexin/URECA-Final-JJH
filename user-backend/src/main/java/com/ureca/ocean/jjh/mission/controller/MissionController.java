package com.ureca.ocean.jjh.mission.controller;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.mission.dto.MissionCompleteDto;
import com.ureca.ocean.jjh.mission.dto.MissionWithConditionDto;
import com.ureca.ocean.jjh.mission.dto.MyMissionDto;
import com.ureca.ocean.jjh.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Tag(name = "Mission", description = "미션 관련 API")
@RequestMapping("api/user")
@RestController @RequiredArgsConstructor @Slf4j
public class MissionController {
    private final MissionService missionService;

    @Operation(summary = "모든 미션 목록 조회", description = "[개발완료] 모든 미션 목록을 가져온다.")
    @GetMapping("/mission")
    public ResponseEntity<BaseResponseDto<?>> getMissions() {
        List<MissionWithConditionDto> userMissions = missionService.getAllMissions();
        log.info("모든 미션 목록 조회");
        return ResponseEntity.ok(BaseResponseDto.success(userMissions));
    }

    @Operation(summary = "내 미션 목록 조회", description = "[개발완료] 로그인 된 계정의 미션 목록을 가져온다. / 실시간으로 가져오기")
    @GetMapping("/mission/my")
    public ResponseEntity<BaseResponseDto<?>> getMyMissions(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "true: 완료된 미션만, false: 미완료 미션만, null: 전체")
            @RequestParam(required = false) Boolean completed
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        List<MyMissionDto> myMissions = missionService.getMyMissions(email, completed);
        log.info("내 미션 목록 조회");
        return ResponseEntity.ok(BaseResponseDto.success(myMissions));
    }

    @Operation(summary = "미션 완료", description = "[개발완료] 조건을 확인 -> 미션 완료 -> 경험치 추가")
    @GetMapping("/mission/complete")
    public ResponseEntity<BaseResponseDto<?>> getMissionComplete(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @RequestParam UUID missionId
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        MissionCompleteDto missionComplete = missionService.getMissionComplete(email, missionId);

        return ResponseEntity.ok(BaseResponseDto.success(missionComplete));
    }
}