package com.ureca.ocean.jjh.store.dto;

import com.ureca.ocean.jjh.store.entity.Store;
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

    // Store → StoreDto 변환용 정적 메서드
    public static StoreDto from(Store store) {
        return StoreDto.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .category(store.getCategory())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .brandName(store.getBrand().getName())
                .brandImageUrl(store.getBrand().getImageUrl())
                .build();
    }
}
