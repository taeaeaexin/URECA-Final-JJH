package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegionUsageDto {

	private UUID userId;
	private String region;
	private Long count;
}
