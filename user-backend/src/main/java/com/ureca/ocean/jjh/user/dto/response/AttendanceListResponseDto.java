package com.ureca.ocean.jjh.user.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class AttendanceListResponseDto {

    private List<LocalDate> attendance;
}
