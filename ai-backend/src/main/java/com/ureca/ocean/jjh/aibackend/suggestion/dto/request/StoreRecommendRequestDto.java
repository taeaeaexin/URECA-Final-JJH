package com.ureca.ocean.jjh.aibackend.suggestion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRecommendRequestDto {
	private String keyword;
    private String category;
    private Double latMin;
    private Double latMax;
    private Double lngMin;
    private Double lngMax;
    private Double centerLat;
    private Double centerLng;
}
