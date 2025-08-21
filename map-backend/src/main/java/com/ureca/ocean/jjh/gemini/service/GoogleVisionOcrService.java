package com.ureca.ocean.jjh.gemini.service;

import org.springframework.web.multipart.MultipartFile;

public interface GoogleVisionOcrService {

    // ocr
    String getOcr(MultipartFile imageFile);
}