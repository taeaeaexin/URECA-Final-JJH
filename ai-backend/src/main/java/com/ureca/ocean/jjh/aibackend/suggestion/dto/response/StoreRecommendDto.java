package com.ureca.ocean.jjh.aibackend.suggestion.dto.response;

import java.util.UUID;

import com.ureca.ocean.jjh.aibackend.client.dto.StoreDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRecommendDto {

	private UUID storeId;
	private String reason;
	
}
