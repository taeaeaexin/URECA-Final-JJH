package com.ureca.ocean.jjh.store.service;

import com.ureca.ocean.jjh.benefit.dto.BenefitDto;
import com.ureca.ocean.jjh.brand.dto.BrandDto;
import com.ureca.ocean.jjh.store.dto.StoreDetailDto;
import com.ureca.ocean.jjh.store.dto.StoreDto;
import com.ureca.ocean.jjh.store.dto.StoreRankDto;
import com.ureca.ocean.jjh.store.dto.StoresDto;
import com.ureca.ocean.jjh.storeUsage.dto.StoreUsageDto;
import com.ureca.ocean.jjh.user.dto.UserRankDto;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface StoreService {
    List<BrandDto> getBrands(String keyword, String sortBy);

    List<StoreDto> getStores(String keyword, String category,
                                    String benefit,
                                    Double latMin, Double latMax,
                                    Double lngMin, Double lngMax,
                                    Double centerLat, Double centerLng);

    List<StoresDto> getStore(String keyword, String category, String benefit);

    StoreDetailDto getDetailStore(UUID storeId);

    List<BenefitDto> getBenefitsByPartnerBrandId(UUID brandId);

    List<StoreUsageDto> getMyStoreUsage(String email, UUID storeId);

    List<String> getListCategory();

    List<BrandDto> getBrandByCategory(String category);

    BrandDto getBrandById(UUID brandId);

    String getBenefitNameById(UUID benefitId);

    List<StoreRankDto> getStoreRankByDesc();

    List<UserRankDto> getUserRankByDesc();
}
