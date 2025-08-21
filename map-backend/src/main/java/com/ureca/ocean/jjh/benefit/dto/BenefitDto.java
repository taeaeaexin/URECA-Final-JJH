package com.ureca.ocean.jjh.benefit.dto;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BenefitDto {
    private UUID id;
    private String name;
    private String category;
    private String description;

    // Benefit → BenefitDto 변환용 정적 메서드
    public static BenefitDto from(Benefit benefit) {
        return BenefitDto.builder()
                .id(benefit.getId())
                .name(benefit.getName())
                .category(benefit.getCategory())
                .description(benefit.getDescription())
                .build();
    }
}
