package com.ureca.ocean.jjh.stat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "user_monthly_usage_stat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMonthlyUsageStat {

	@Id
	private String id;
	private UUID userId;
	private int year;
	private int month;
	private int count;
	private int amount;
	private LocalDateTime updatedAt;
	
}
