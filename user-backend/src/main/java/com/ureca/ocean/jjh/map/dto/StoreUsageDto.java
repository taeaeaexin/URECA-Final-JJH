package com.ureca.ocean.jjh.map.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StoreUsageDto {
    private UUID id;
    private UUID userId;
    private String storeId;
    private UUID benefitId;
    private LocalDateTime visitedAt;
    private String brandName;
    private int benefitAmount;
}
