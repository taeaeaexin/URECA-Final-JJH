package com.ureca.ocean.jjh.storeUsage.service;

import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;
import com.ureca.ocean.jjh.storeUsage.dto.StoreUsageDto;

public interface StoreUsageService {
    StoreUsageDto saveStoreUsage(String email, OcrGeminiResponseDto dto);
}
