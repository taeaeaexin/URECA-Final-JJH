package com.ureca.ocean.jjh.store.repository;

import com.ureca.ocean.jjh.store.entity.Store;

import java.util.List;

public interface StoreRepositoryCustom {
    // 주변 매장 검색
    List<Store> searchStores(
            String keyword,
            String category,
            String benefit,
            Double latMin,
            Double latMax,
            Double lngMin,
            Double lngMax,
            Double centerLat,
            Double centerLng
    );

    List<Store> searchStore(String keyword, String category, String benefit);
}
