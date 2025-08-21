package com.ureca.ocean.jjh.gemini.service;

import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;

public interface GeminiService {

    // gemini 채팅 테스트
    String geminiChat(String message);

    // ocrText -> gemini -> Json
    OcrGeminiResponseDto OcrTextToJson(String ocrText, String email);
}