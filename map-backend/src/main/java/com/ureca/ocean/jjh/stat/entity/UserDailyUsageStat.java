package com.ureca.ocean.jjh.stat.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "user_daily_usage_stat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDailyUsageStat {

	@Id
	private String id;
	private UUID userId;
	private LocalDate date;
	private int count;
	private int amount;
	private LocalDateTime updatedAt;
	
}
