package com.ureca.ocean.jjh.storeUsage.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.ocean.jjh.benefit.entity.QBenefit;
import com.ureca.ocean.jjh.store.entity.QStore;
import com.ureca.ocean.jjh.storeUsage.entity.QStoreUsage;
import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import com.ureca.ocean.jjh.storeUsage.repository.StoreUsageRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StoreUsageRepositoryImpl implements StoreUsageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreUsage> findByUserIdAndOptionalStoreId(UUID userId, UUID storeId) {
        QStoreUsage usage = QStoreUsage.storeUsage;
        QBenefit benefit = QBenefit.benefit;
        QStore store = QStore.store;

        BooleanExpression userCondition = usage.userId.eq(userId);
        BooleanExpression storeCondition = (storeId != null)
                ? usage.store.id.eq(storeId)
                : null;

        return queryFactory
                .selectFrom(usage)
                .join(usage.store, store).fetchJoin()
                .join(usage.benefit, benefit).fetchJoin()
                .where(userCondition, storeCondition)
                .fetch();
    }
}
