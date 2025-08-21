package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyUsageDto {

	private UUID userId;
    private Integer year;
    private Integer month;
    private Integer week;
    private Long count;
    private Long amount;
}
