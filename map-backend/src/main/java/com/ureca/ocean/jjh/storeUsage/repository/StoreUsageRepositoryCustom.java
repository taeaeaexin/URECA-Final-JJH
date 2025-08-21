package com.ureca.ocean.jjh.storeUsage.repository;

import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;

import java.util.List;
import java.util.UUID;

public interface StoreUsageRepositoryCustom {
    List<StoreUsage> findByUserIdAndOptionalStoreId(UUID userId, UUID storeId);
}
