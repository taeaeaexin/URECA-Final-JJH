package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandUsageDto {

	private UUID userId;
	private String brandName;
	private Long count;
}
