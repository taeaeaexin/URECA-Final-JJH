package com.ureca.ocean.jjh.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
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
