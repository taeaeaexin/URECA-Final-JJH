package com.ureca.ocean.jjh.batch.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryUsageDto {

	private UUID userId;
    private String category;
    private Long count;
}
