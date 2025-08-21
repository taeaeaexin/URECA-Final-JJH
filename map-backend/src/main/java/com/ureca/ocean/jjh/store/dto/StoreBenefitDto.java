package com.ureca.ocean.jjh.store.dto;

import com.ureca.ocean.jjh.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StoreBenefitDto {
    private UUID id;
    private String name;
    private String address;
    private String category;
    private List<String> benefit;
    private Double latitude;
    private Double longitude;

    private String brandName;
    private String brandImageUrl;

    // Store → StoreBenefitDto 변환용 정적 메서드
    public static StoreBenefitDto from(Store store, List<String> benefits) {
        return StoreBenefitDto.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .category(store.getCategory())
                .benefit(benefits)
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .brandName(store.getBrand().getName())
                .brandImageUrl(store.getBrand().getImageUrl())
                .build();
    }
}
