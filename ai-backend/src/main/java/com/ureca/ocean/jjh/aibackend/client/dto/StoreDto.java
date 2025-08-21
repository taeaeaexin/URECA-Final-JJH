package com.ureca.ocean.jjh.aibackend.client.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {

	private UUID id;
    private String name;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;

    private String brandName;
    private String brandImageUrl;
}
