package com.ureca.ocean.jjh.store.dto;

import com.ureca.ocean.jjh.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StoresDto {
    private UUID id;
    private String name;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;

    private String brandName;
    private String brandImageUrl;

    // Stores → StoresDto 변환용 정적 메서드
    public static StoresDto from(Store store) {
        return StoresDto.builder()
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
