package com.ureca.ocean.jjh.gemini.service.impl;

import com.ureca.ocean.jjh.gemini.service.GoogleVisionOcrService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// CI/CD Test 통과용 (중요한 친구임 건들면 큰일)
@Service
@ConditionalOnProperty(name = "vision.key.env", havingValue = "dummy", matchIfMissing = true)
public class DummyOcrServiceImpl implements GoogleVisionOcrService {
    @Override
    public String getOcr(MultipartFile imageFile) {
        System.out.println("DummyOcrService : 테스트 중이라 OCR 호출을 우회합니다.");
        return "테스트용 OCR 결과";
    }
}
