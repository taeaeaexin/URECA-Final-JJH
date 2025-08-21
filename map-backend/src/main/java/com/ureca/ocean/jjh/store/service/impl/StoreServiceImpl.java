package com.ureca.ocean.jjh.store.service.impl;

import com.ureca.ocean.jjh.benefit.dto.BenefitDto;
import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.benefit.repository.BenefitRepository;
import com.ureca.ocean.jjh.brand.dto.BrandDto;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.store.dto.*;
import com.ureca.ocean.jjh.brand.entity.Brand;
import com.ureca.ocean.jjh.store.entity.Store;
import com.ureca.ocean.jjh.brand.repository.BrandRepository;
import com.ureca.ocean.jjh.store.repository.StoreRepository;
import com.ureca.ocean.jjh.store.service.StoreService;
import com.ureca.ocean.jjh.storeUsage.dto.StoreUsageDto;
import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import com.ureca.ocean.jjh.storeUsage.repository.StoreUsageRepository;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserAndStatusResponseDto;
import com.ureca.ocean.jjh.user.dto.UserDto;
import com.ureca.ocean.jjh.user.dto.UserRankDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final BrandRepository brandRepository;
    private final BenefitRepository benefitRepository;
    private final StoreUsageRepository storeUsageRepository;
    private final UserClient userClient;

    // 제휴 브랜드 목록 조회
    @Override
    public List<BrandDto> getBrands(String keyword, String sortBy) {
        Sort sort = "desc".equalsIgnoreCase(sortBy)
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();

        List<Brand> brands = (keyword != null && !keyword.isBlank())
                ? brandRepository.findByNameContaining(keyword, sort)
                : brandRepository.findAll(sort);

        // exception 처리
        if (brands == null || brands.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        // List<Brand> -> BrandDto
        return brands.stream().map(BrandDto::from).toList();
    }

    // 제휴처 목록 조회
    @Override
    public List<StoreDto> getStores(String keyword,
                                               String category, String benefit,
                                               Double latMin, Double latMax,
                                               Double lngMin, Double lngMax,
                                               Double centerLat, Double centerLng
    ) {
        List<Store> stores = storeRepository.searchStores(
                keyword, category, benefit, latMin, latMax, lngMin, lngMax, centerLat, centerLng
        );

        // exception 처리
        if (stores == null || stores.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        // List<Store> -> StoreBenefitDto
        return stores.stream().map(StoreDto::from).toList();
    }

    // 제휴처 목록 조회2
    @Override
    public List<StoresDto> getStore(String keyword, String category, String benefit) {
        List<Store> stores = storeRepository.searchStore(keyword, category, benefit);

        // exception
        if(stores == null || stores.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        return stores.stream().map(StoresDto::from).toList();
    }

    // 제휴처 상세 조회
    @Override
    public StoreDetailDto getDetailStore(UUID storeId) {
        Optional<Store> store = storeRepository.findById(storeId);

        // exception 처리
        if(store.isEmpty()){
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }
        Store foundStore = store.get();
        List<String> benefitCategories = foundStore.getBrand().getBenefits()
            .stream()
            .map(Benefit::getCategory)
            .distinct()
            .toList();
        return StoreDetailDto.from(foundStore, benefitCategories);
    }

    // 브랜드 별 혜택 조회
    @Override
    public List<BenefitDto> getBenefitsByPartnerBrandId(UUID brandId) {
        List<Benefit> benefits = benefitRepository.getBenefitByBrandId(brandId);

        // exception 처리
        if (benefits == null || benefits.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        // List<Benefit> -> BenefitDto
        return benefits.stream().map(BenefitDto::from).toList();
    }

    // 내 혜택 사용 내역 조회
    @Override
    public List<StoreUsageDto> getMyStoreUsage(String email, UUID storeId) {
        UserDto user = userClient.getUserByEmail(email);

        if (user == null || user.getId() == null) {
            throw new MapException(ErrorCode.USER_ID_PARSING_ERROR);
        }

        List<StoreUsage> storeUsages = (storeId == null)
                ? storeUsageRepository.findAllByUserId(user.getId())
                : storeUsageRepository.findByUserIdAndOptionalStoreId(user.getId(), storeId);

        return storeUsages.stream().map(StoreUsageDto::from).toList();
    }

    @Override
    public List<String> getListCategory(){
        return storeRepository.findDistinctCategories();
    }

    @Override
    public List<BrandDto> getBrandByCategory(String category) {
        return storeRepository.findDistinctBrandByCategory(category).stream()
                .map(BrandDto::from)
                .toList();
    }

    @Override
    public BrandDto getBrandById(UUID brandId){
        return BrandDto.from(brandRepository.findById(brandId).orElseThrow(()->new MapException(ErrorCode.NOT_FOUND_BRAND)));
    }

    @Override
    public String getBenefitNameById(UUID benefitId){
      return benefitRepository.findById(benefitId).orElseThrow(()->new MapException(ErrorCode.NOT_FOUND_BENEFIT)).getName();
    }

    // store rank
    @Override
    public List<StoreRankDto> getStoreRankByDesc() {
        List<StoreRankDto> storeRankList = storeRepository.findStoreByStoreUsageDesc();

        // exception 처리
        if(storeRankList == null || storeRankList.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        return storeRankList;
    }

    // user rank
    @Override
    public List<UserRankDto> getUserRankByDesc() {
        List<UserAndStatusResponseDto> users = userClient.getAllUserAndStatus();

        if (users == null || users.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }

        int totalStoreCount = brandRepository.countAllBrand();
        double maxVisitCount = totalStoreCount * 20.0;

        return users.stream()
                .map(user -> {
                    List<StoreUsage> usages = storeUsageRepository.findAllByUserId(user.getId());

                    Map<String, Integer> brandVisitCount = new HashMap<>();
                    for (StoreUsage usage : usages) {
                        String brand = usage.getStore().getBrand().getName().toString();

                        brandVisitCount.put(
                                brand,
                                Math.min(20, brandVisitCount.getOrDefault(brand, 0) + 1)
                        );
                    }

                    int totalVisitCount = brandVisitCount.values().stream().mapToInt(Integer::intValue).sum();
                    double progressPercentage = (totalVisitCount / maxVisitCount) * 100;
                    int usageCount = usages.size();

                    return UserRankDto.from(user, progressPercentage, usageCount);
                })
                .sorted(Comparator.comparingInt(UserRankDto::getStoreUsage).reversed())
                .toList();
    }
}
