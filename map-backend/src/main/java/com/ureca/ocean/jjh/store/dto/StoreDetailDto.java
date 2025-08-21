package com.ureca.ocean.jjh.store.dto;

import com.ureca.ocean.jjh.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StoreDetailDto {
    private UUID id;
    private String name;
    private String address;
    private String category;
    private List<String> benefit;
    private Double latitude;
    private Double longitude;

    private String brandName;
    private String brandImageUrl;

    // Store → StoreDto 변환용 정적 메서드
    public static StoreDetailDto from(Store store, List<String> benefitCategories) {
        return StoreDetailDto.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .category(store.getCategory())
                .benefit(benefitCategories)
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .brandName(store.getBrand().getName())
                .brandImageUrl(store.getBrand().getImageUrl())
                .build();
    }
}
