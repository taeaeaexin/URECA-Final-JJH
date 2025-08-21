package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeekdayUsageDto {

	private UUID userId;
	private String weekdayName;
	private Long count;
}
