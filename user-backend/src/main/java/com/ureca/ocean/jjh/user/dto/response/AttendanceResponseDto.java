package com.ureca.ocean.jjh.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter @Setter
public class AttendanceResponseDto {
    private UUID userId;
    private LocalDate date;
}
