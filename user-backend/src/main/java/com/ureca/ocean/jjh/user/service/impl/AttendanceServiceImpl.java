package com.ureca.ocean.jjh.user.service.impl;

import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.user.dto.response.AttendanceListResponseDto;
import com.ureca.ocean.jjh.user.dto.response.AttendanceResponseDto;
import com.ureca.ocean.jjh.user.entity.Attendance;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.repository.AttendanceRepository;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import com.ureca.ocean.jjh.user.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public AttendanceResponseDto insertAttendance(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.NOT_FOUND_USER));
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        log.info("📌 [InsertAttendance] 오늘 날짜 (KST 기준): {}", today);

        if(attendanceRepository.existsByDateAndUser(today, user)){
            throw new UserException(ErrorCode.ATTENDANCE_ALREADY_DONE);
        }
        Attendance attendance = Attendance.builder()
                .date(today)
                .user(user)
                .build();

        Attendance attendanceResult = attendanceRepository.save(attendance);


        return AttendanceResponseDto.builder()
                .userId(attendanceResult.getUser().getId())
                .date(attendanceResult.getDate())
                .build();

    }
    @Override
    public AttendanceListResponseDto listAttendance(String email, int year, int month){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.NOT_FOUND_USER));

        List<Attendance> attendanceResult = attendanceRepository.findByMonthAndYear(year,month,user);

        List<LocalDate> localDates = attendanceResult.stream()
                .map(Attendance::getDate)
                .collect(Collectors.toList());

        return AttendanceListResponseDto.builder()
                .attendance(localDates)
                .build();
    }
}

