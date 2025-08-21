package com.ureca.ocean.jjh.store.dto;

import com.ureca.ocean.jjh.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StoreRankDto {
    private UUID id;
    private String name;
    private int usageCount;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;

    private String brandName;
    private String brandImageUrl;

    // Store → StoreDto 변환용 정적 메서드
    public static StoreRankDto from(Store store, int usageCount) {
        return StoreRankDto.builder()
                .id(store.getId())
                .name(store.getName())
                .usageCount(usageCount)
                .address(store.getAddress())
                .category(store.getCategory())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .brandName(store.getBrand().getName())
                .brandImageUrl(store.getBrand().getImageUrl())
                .build();
    }

    public StoreRankDto(UUID id, String name, long usageCount, String address,
                        String category, Double latitude, Double longitude,
                        String brandName, String brandImageUrl) {
        this.id = id;
        this.name = name;
        this.usageCount = (int) usageCount;
        this.address = address;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.brandName = brandName;
        this.brandImageUrl = brandImageUrl;
    }
}
