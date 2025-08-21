package com.ureca.ocean.jjh.user.service;

import com.ureca.ocean.jjh.user.dto.response.AttendanceListResponseDto;
import com.ureca.ocean.jjh.user.dto.response.AttendanceResponseDto;

import java.util.UUID;

public interface AttendanceService {

    AttendanceResponseDto insertAttendance(String email);

    AttendanceListResponseDto listAttendance(String email, int year, int month);
}
