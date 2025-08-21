package com.ureca.ocean.jjh.brand.dto;

import com.ureca.ocean.jjh.brand.entity.Brand;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BrandDto {
    private UUID id;
    private String name;
    private String image_url;

    // Brand → BrandDto 변환용 정적 메서드
    public static BrandDto from(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image_url(brand.getImageUrl())
                .build();
    }
}