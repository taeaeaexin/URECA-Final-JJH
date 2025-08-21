package com.ureca.ocean.jjh.batch.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class DailyUsageDto {

	private UUID userId;
    private LocalDate date;
    private Long count;
    private Long amount;
    
    public DailyUsageDto(UUID userId, Date date, Long count, Long amount) {
        this.userId = userId;
        this.date = ((java.sql.Date) date).toLocalDate(); // ✅ 안전하게 변환
        this.count = count;
        this.amount = amount;
    }
	
}
