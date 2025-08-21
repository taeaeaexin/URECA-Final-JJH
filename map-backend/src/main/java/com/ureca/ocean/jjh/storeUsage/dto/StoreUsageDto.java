package com.ureca.ocean.jjh.storeUsage.dto;

import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StoreUsageDto {
    private UUID id;
    private UUID userId;
    private String storeId;
    private String brandName;
    private UUID benefitId;
    private LocalDateTime visitedAt;
    private int benefitAmount;

    // StoreUsage → StoreUsageDto 변환용 정적 메서드
    public static StoreUsageDto from(StoreUsage storeUsage) {
        return StoreUsageDto.builder()
                .id(storeUsage.getId())
                .userId(storeUsage.getUserId())
                .storeId(storeUsage.getStore().getName())
                .brandName(storeUsage.getStore().getBrand().getName())
                .benefitId(storeUsage.getBenefit().getId())
                .visitedAt(storeUsage.getVisitedAt())
                .benefitAmount(storeUsage.getBenefitAmount())
                .build();
    }
}