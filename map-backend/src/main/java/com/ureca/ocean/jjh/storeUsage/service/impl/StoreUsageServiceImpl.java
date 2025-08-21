package com.ureca.ocean.jjh.storeUsage.service.impl;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.benefit.repository.BenefitRepository;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;
import com.ureca.ocean.jjh.store.entity.Store;
import com.ureca.ocean.jjh.store.repository.StoreRepository;
import com.ureca.ocean.jjh.storeUsage.dto.StoreUsageDto;
import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import com.ureca.ocean.jjh.storeUsage.repository.StoreUsageRepository;
import com.ureca.ocean.jjh.storeUsage.service.StoreUsageService;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserAndMembershipDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreUsageServiceImpl implements StoreUsageService {
    private final StoreUsageRepository storeUsageRepository;
    private final StoreRepository storeRepository;
    private final UserClient userClient;
    private final BenefitRepository benefitRepository;

    @Override
    public StoreUsageDto saveStoreUsage(String email, OcrGeminiResponseDto dto) {
        UserAndMembershipDto user = userClient.getUserAndMembershipByEmail(email);

        // 가게 이름으로 Store 검색, 없으면 예외 처리
        List<Store> storeList = storeRepository.searchStores(
                dto.getStoreName(), null, null, null, null, null, null, null, null);
        if (storeList.isEmpty()) {
            throw new MapException(ErrorCode.NOT_FOUND_ERROR);
        }
        Store store = storeList.get(0); // 첫 번째 결과 사용

        boolean exists = storeUsageRepository.existsByUserIdAndStoreAndVisitedAt(
            user.getId(), store, dto.getVisitedAt()
        );
        if (exists) {
            throw new MapException(ErrorCode.ALREADY_SAVED_BILL);
        }

        Benefit benefit = benefitRepository
                .findBenefitByBrandIdAndMembership(
                        store.getBrand().getId(),
                        user.getMembership()
                )
                .orElseThrow(() -> new MapException(ErrorCode.NOT_FOUND_BENEFIT));

        // StoreUsage 객체 생성 및 저장
        StoreUsage usage = StoreUsage.from(dto, user.getId(), store, benefit, dto.getBenefitAmount());
        storeUsageRepository.save(usage);

        return StoreUsageDto.from(usage);
    }
}