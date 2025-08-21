package com.ureca.ocean.jjh.user.controller;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.exception.ErrorCode;

import com.ureca.ocean.jjh.user.dto.request.SignUpRequestDto;
import com.ureca.ocean.jjh.user.dto.request.UserRequestDto;
import com.ureca.ocean.jjh.user.dto.request.UserStatusRequestDto;
import com.ureca.ocean.jjh.user.dto.response.*;
import com.ureca.ocean.jjh.user.service.UserService;
import com.ureca.ocean.jjh.user.service.impl.AttendanceServiceImpl;
import com.ureca.ocean.jjh.user.service.impl.UserServiceImpl;
import com.ureca.ocean.jjh.user.service.impl.UserStatusServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final UserStatusServiceImpl userStatusServiceImpl;
    private final AttendanceServiceImpl attendanceServiceImpl;
    private final UserService userService;

    /**
     * 서비스 상태 확인용 엔드포인트
     */
    @Operation(summary = "사용자 서비스 상태 확인")
    @PostMapping("/health")
    public String health() {
        log.info("user health checking...");
        return "사용자 서비스 상태 정상";
    }

    /**
     * 이메일로 사용자 정보 조회
     */
    @Operation(summary = "이메일로 사용자 정보 조회")
    @GetMapping
    public ResponseEntity<BaseResponseDto<?>> getUserByEmail(
            @Parameter(description = "사용자 이메일") @RequestParam(name = "email") String email) {
        UserResponseDtoWithPassword userResponseDtoWithPassword = userServiceImpl.getUserByEmail(email);
        if (userResponseDtoWithPassword.getId() == null) {
            return ResponseEntity.badRequest().body(BaseResponseDto.fail(ErrorCode.NOT_FOUND_USER));
        }
        return ResponseEntity.ok(BaseResponseDto.success(userResponseDtoWithPassword));
    }

    /**
     * 닉네임 중복 여부 확인
     */
    @Operation(summary = "닉네임 중복 여부 확인")
    @GetMapping("/isDupNickname")
    public ResponseEntity<BaseResponseDto<?>> getIsDupNickname(
            @Parameter(description = "중복 확인할 닉네임") @RequestParam String nickname) {
        return ResponseEntity.ok(BaseResponseDto.success(userServiceImpl.getIsDupNickname(nickname)));
    }

    /**
     * 이메일 중복 여부 확인
     */
    @Operation(summary = "이메일 중복 여부 확인")
    @GetMapping("/isDupEmail")
    public ResponseEntity<BaseResponseDto<?>> getIsDupEmail(
            @Parameter(description = "중복 확인할 이메일") @RequestParam String email) {
        return ResponseEntity.ok(BaseResponseDto.success(userServiceImpl.getIsDupEmail(email)));
    }

    /**
     * 사용자 회원가입
     */
    @Operation(summary = "사용자 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponseDto<?>> signUp(
            @Parameter(description = "회원가입 요청 정보") @RequestBody SignUpRequestDto signUpRequestDto) {
        UserResponseDto userDto = userServiceImpl.signUp(signUpRequestDto);
        return ResponseEntity.ok(BaseResponseDto.success(userDto));
    }

    /**
     * 현재 로그인한 사용자 정보 조회 (헤더에서 이메일 가져옴)
     */
    @Operation(summary = "현재 로그인한 사용자 정보 조회")
    @PostMapping("/currentUserInfo")
    public ResponseEntity<BaseResponseDto<?>> getCurrentUserInfo(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        UserResponseDto userDto = userServiceImpl.getCurrentUserInfo(email);
        if (userDto.getId() == null) {
            return ResponseEntity.badRequest().body(BaseResponseDto.fail(ErrorCode.NOT_FOUND_USER));
        }
        return ResponseEntity.ok(BaseResponseDto.success(userDto));
    }

    /**
     * 사용자 상태 조회
     */
    @Operation(summary = "사용자 상태 조회")
    @GetMapping("/stat")
    public ResponseEntity<BaseResponseDto<?>> getUserStatus(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        UserStatusResponseDto userStatusResponseDto = userStatusServiceImpl.getUserStatus(email);
        return ResponseEntity.ok(BaseResponseDto.success(userStatusResponseDto));
    }

    /**
     * 사용자 상태 수정
     */
    @Operation(summary = "사용자 상태 수정")
    @PutMapping("/stat")
    public ResponseEntity<BaseResponseDto<?>> modifyUserStatus(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "사용자 상태 변경 요청") @RequestBody UserStatusRequestDto userStatusRequestDto) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        UserStatusResponseDto userStatusResponseDto = userStatusServiceImpl.changeUserStatus(
                email, userStatusRequestDto.getExpChange());
        return ResponseEntity.ok(BaseResponseDto.success(userStatusResponseDto));
    }

    /**
     * 출석 기록 추가
     */
    @Operation(summary = "출석 기록 추가")
    @PostMapping("/attendance")
    public ResponseEntity<BaseResponseDto<?>> insertAttendance(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        AttendanceResponseDto userStatusResponseDto = attendanceServiceImpl.insertAttendance(email);
        return ResponseEntity.ok(BaseResponseDto.success(userStatusResponseDto));
    }

    /**
     * 년도와 월별 출석 기록 목록 조회
     */
    @Operation(summary = "년도 및 월별 출석 기록 조회")
    @GetMapping("/attendance")
    public ResponseEntity<BaseResponseDto<?>> listAttendance(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "년도 (예: 2025)") @RequestParam Integer year,
            @Parameter(description = "월 (1~12)") @RequestParam Integer month) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        AttendanceListResponseDto attendanceListResponseDto = attendanceServiceImpl.listAttendance(email, year, month);
        return ResponseEntity.ok(BaseResponseDto.success(attendanceListResponseDto));
    }

    /**
     * 사용자 정보 수정
     */
    @Operation(summary = "사용자 정보 수정")
    @PutMapping
    public ResponseEntity<BaseResponseDto<?>> updateUserInfo(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "사용자 정보 수정 요청") @RequestBody UserRequestDto userRequestDto) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 현재 사용자 이메일 : " + email);
        UserResponseDto userResponseDto = userServiceImpl.updateUserInfo(
                email,
                userRequestDto);
        return ResponseEntity.ok(BaseResponseDto.success(userResponseDto));
    }

    /**
     * Get All Users and Status
     */
    @Operation(summary = "Get All Users and Status")
    @GetMapping("status")
    public ResponseEntity<BaseResponseDto<?>> getAllUsersAndStatus() {
        List<UserAndStatusResponseDto> result = userService.getAllUserAndStatus();

        return ResponseEntity.ok(BaseResponseDto.success(result));
    }
}
