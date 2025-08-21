package com.ureca.ocean.jjh.benefit.dto;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.common.enums.Membership;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BenefitsDto {
    private UUID id;
    private String name;
    private String category;
    private Membership membership;
    private int benefit_value;
    private String benefit_unit;
    private String description;

    // Benefit → BenefitsDto 변환용 정적 메서드
    public static BenefitsDto from(Benefit benefit) {
        return BenefitsDto.builder()
                .id(benefit.getId())
                .name(benefit.getName())
                .category(benefit.getCategory())
                .membership(benefit.getMembership())
                .benefit_value(benefit.getBenefitValue())
                .benefit_unit(benefit.getBenefitUnit())
                .description(benefit.getDescription())
                .build();
    }
}
