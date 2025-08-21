package com.ureca.ocean.jjh.aibackend.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreUsageDto {
	private UUID id;
    private UUID userId;
    private String storeId;
    private UUID benefitId;
    private LocalDateTime visitedAt;
    private int benefit;
}
