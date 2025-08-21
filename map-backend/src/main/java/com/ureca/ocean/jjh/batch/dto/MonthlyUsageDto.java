package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyUsageDto {
	
	private UUID userId;
    private int year;
    private int month;
    private Long count;
    private Long amount;

}
