package com.ureca.ocean.jjh.gemini.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OcrGeminiResponseDto {
        private String storeName;
        private String partnerBrand;
        private String category;
        private String address;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime visitedAt;

        private int totalAmount;
        private int benefitAmount;
}