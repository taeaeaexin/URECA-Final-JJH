package com.ureca.ocean.jjh.gemini.service.impl;

import com.ureca.ocean.jjh.brand.entity.Brand;
import com.ureca.ocean.jjh.brand.repository.BrandRepository;
import com.ureca.ocean.jjh.common.enums.Membership;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;
import com.ureca.ocean.jjh.gemini.service.GeminiService;
import com.ureca.ocean.jjh.user.client.UserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GeminiServiceImpl implements GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiServiceImpl.class);
    private final BrandRepository brandRepository;
    private final UserClient userClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private final String curl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private final WebClient webClient = WebClient.builder()
            .baseUrl(curl)
            .build();

    private final ObjectMapper objectMapper;

    @Override
    public String geminiChat(String message) {
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", message)
                ))
            )
        );

        try {
            String rawResponse = webClient.post()
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", geminiApiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(rawResponse);
            return root.at("/candidates/0/content/parts/0/text").asText("결과 파싱 실패");
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    @Override
    public OcrGeminiResponseDto OcrTextToJson(String ocrText, String email) {
        List<String> partnerBrands = getPartnerBrands();
        String prompt = OcrTextToJsonPrompt(ocrText, partnerBrands);
        String ocrJson = geminiChat(prompt);

        String resultJson = null;
        try {
            // ```json ~ ``` 코드블럭 제거
            ocrJson = ocrJson.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();
            OcrGeminiResponseDto result = objectMapper.readValue(ocrJson, OcrGeminiResponseDto.class);

            // gemini 재진입
            Membership membership = userClient.getUserAndMembershipByEmail(email).getMembership();
            String benefit = result.getPartnerBrand();
            String secondPrompt = OcrResultPrompt(result, membership, benefit);
            resultJson = geminiChat(secondPrompt);
            resultJson = resultJson.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();
            OcrGeminiResponseDto finalResult = objectMapper.readValue(resultJson, OcrGeminiResponseDto.class);
            // partnerBrand가 "아님"인 경우 benefitAmount = 0으로 설정
            if ("아님".equals(finalResult.getPartnerBrand())) {
                finalResult.setBenefitAmount(0);
            }
            return finalResult;
        } catch (Exception e) {
            throw new MapException(ErrorCode.OCR_PROCESSING_FAIL);
        }
    }

    // 파트너 브랜드 목록을 조회
    private List<String> getPartnerBrands() {
        return brandRepository.findAll()
                        .stream()
                        .map(Brand::getName)
                        .toList();
    }

    // 파트너 브랜드 목록을 받아 프롬프트에 명시
    private String OcrTextToJsonPrompt(String ocrText, List<String> partnerBrands) {
        return """
        아래는 영수증에서 추출한 텍스트입니다. 이를 기반으로 JSON으로 구조화해 주세요.

        반드시 이 형식을 지켜주세요:
        {
          "storeName": string,
          "partnerBrand" : string,
          "category": string,
          "address": string,
          "visitedAt": "yyyy-MM-dd'T'HH:mm:ss",
          "totalAmount": number
        }
        
        카테고리는 아래 6개 중 하나로 선택
        category : 편의점, 음식점, 카페, 대형마트, 문화시설, 렌터카

        partnerBrand는 아래 중 하나로 선택
        %s
        먄약 없으면 "아님"

        텍스트:
        """.formatted(String.join(", ", partnerBrands)) + ocrText;
    }

    private String OcrResultPrompt(OcrGeminiResponseDto ocr, Membership membership, String benefit) {
        return """
        아래는 영수증에서 추출한 텍스트입니다. 이를 기반으로 JSON으로 구조화해 주세요.

        반드시 이 형식을 지켜주세요:
        {
          "storeName": string,
          "partnerBrand" : string,
          "category": string,
          "address": string,
          "visitedAt": "yyyy-MM-dd'T'HH:mm:ss",
          "totalAmount": number,
          "benefitAmount": number
        }

        할인 정보
        - 나의 멤버십 등급: %s
        - 제휴 브랜드의 할인 혜택: %s

        위 혜택을 totalAmount에 적용해 benefitAmount 값을 계산해 주세요.
        단, 제휴 브랜드가 "아님"이라면 benefitAmount는 반드시 0으로 설정해 주세요.

        참고용 영수증 정보:
        {
          "storeName": "%s",
          "partnerBrand" : "%s",
          "category": "%s",
          "address": "%s",
          "visitedAt": "%s",
          "totalAmount": %d
        }
        """.formatted(
            membership.name(),
            benefit,
            ocr.getStoreName(),
            ocr.getPartnerBrand(),
            ocr.getCategory(),
            ocr.getAddress(),
            ocr.getVisitedAt(),
            ocr.getTotalAmount()
        );
    }
}
