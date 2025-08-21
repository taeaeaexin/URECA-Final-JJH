package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalUsageDto {
	UUID userId;
	Long count;
	Long amount;
}
