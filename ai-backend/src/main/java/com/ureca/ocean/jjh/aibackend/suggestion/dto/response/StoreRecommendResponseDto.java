package com.ureca.ocean.jjh.aibackend.suggestion.dto.response;

import com.ureca.ocean.jjh.aibackend.client.dto.StoreDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRecommendResponseDto {

	private StoreDto store;
	private String reason;
	
}
