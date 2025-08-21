package com.ureca.ocean.jjh.store.repository.impl;

import lombok.extern.slf4j.Slf4j;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.ureca.ocean.jjh.benefit.entity.QBenefit;
import com.ureca.ocean.jjh.brand.entity.QBrand;
import com.ureca.ocean.jjh.store.entity.Store;
import com.ureca.ocean.jjh.store.entity.QStore;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.ocean.jjh.store.repository.StoreRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> searchStores(
            String keyword,
            String category,
            String benefit,
            Double latMin,
            Double latMax,
            Double lngMin,
            Double lngMax,
            Double centerLat,
            Double centerLng
    ) {
        QStore store = QStore.store;
        QBrand brand = QBrand.brand;
        QBenefit qBenefit = QBenefit.benefit;

        // 공통 조건
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.or(store.name.containsIgnoreCase(keyword));
            builder.or(store.address.containsIgnoreCase(keyword));
            builder.or(store.category.containsIgnoreCase(keyword));
            builder.or(brand.name.containsIgnoreCase(keyword));
        }

        if (category != null && !category.isBlank()) {
            builder.and(store.category.eq(category));
        }

        String benefitCategory = null;
        if (benefit != null && !benefit.isBlank()) {
            Map<String, String> benefitCategoryMap = Map.of(
                "쿠폰", "coupon",
                "할인", "discount",
                "증정", "free_item"
            );
            benefitCategory = benefitCategoryMap.getOrDefault(benefit, benefit);
            builder.and(store.brand.benefits.any().category.equalsIgnoreCase(benefitCategory));
        }

        // 매장 + 브랜드 + 혜택 fetch join
        JPAQuery<Store> baseQuery = queryFactory
                .selectFrom(store)
                .leftJoin(store.brand, brand).fetchJoin()
                .leftJoin(brand.benefits, qBenefit).fetchJoin()
                .where(builder);

        // 위치 범위가 주어졌을 경우
        if (latMin != null && latMax != null && lngMin != null && lngMax != null) {
            List<Store> inBoundStores = baseQuery
                    .where(
                            store.latitude.between(latMin, latMax)
                                    .and(store.longitude.between(lngMin, lngMax))
                    )
                    .fetch();

            if (!inBoundStores.isEmpty()) {
                return inBoundStores;
            } else if (centerLat != null && centerLng != null) {
                // 범위 내 매장이 없으면 중심좌표 기준 가장 가까운 매장 1개 반환
                return queryFactory
                        .selectFrom(store)
                        .leftJoin(store.brand, brand).fetchJoin()
                        .leftJoin(brand.benefits, qBenefit).fetchJoin()
                        .where(builder)
                        .orderBy(distance(store.latitude, store.longitude, centerLat, centerLng).asc())
                        .limit(1)
                        .fetch();
            }
        }

        // 위치조건 없이 전체 검색
        return baseQuery.fetch();
    }

    @Override
    public List<Store> searchStore(String keyword, String category, String benefit) {
        QStore store = QStore.store;
        QBenefit qBenefit = QBenefit.benefit;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(store.name.containsIgnoreCase(keyword)
                    .or(store.address.containsIgnoreCase(keyword)));
        }
        if (category != null && !category.isBlank()) {
            builder.and(store.category.eq(category));
        }
        if (benefit != null && !benefit.isBlank()) {
            Map<String, String> benefitCategoryMap = Map.of(
                "쿠폰", "coupon",
                "할인", "discount",
                "증정", "free_item"
            );
            String benefitCategory = benefitCategoryMap.getOrDefault(benefit, benefit);
            builder.and(store.brand.benefits.any().category.equalsIgnoreCase(benefitCategory));
        }

        return queryFactory
                .selectFrom(store)
                .join(store.brand, QBrand.brand).fetchJoin()
                .where(builder)
                .fetch();
    }

    // 범위 안에 매장이 없을 시 가장 가까운 매장 검색-Haversine 공식
    private NumberExpression<Double> distance(NumberPath<Double> lat, NumberPath<Double> lng, double centerLat, double centerLng) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                centerLat, lat, lng, centerLng
        );
    }
}