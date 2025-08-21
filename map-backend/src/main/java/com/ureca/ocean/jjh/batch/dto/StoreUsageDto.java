package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreUsageDto {
	
	private UUID userId;
    private String storeName;
    private Long count;
}
