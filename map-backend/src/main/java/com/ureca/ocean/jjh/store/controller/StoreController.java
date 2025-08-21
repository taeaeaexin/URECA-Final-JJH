package com.ureca.ocean.jjh.store.controller;

import com.ureca.ocean.jjh.benefit.dto.BenefitDto;
import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.brand.dto.BrandDto;
import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;
import com.ureca.ocean.jjh.gemini.service.GeminiService;
import com.ureca.ocean.jjh.gemini.service.GoogleVisionOcrService;
import com.ureca.ocean.jjh.store.dto.*;
import com.ureca.ocean.jjh.store.service.StoreService;
import com.ureca.ocean.jjh.storeUsage.dto.StoreUsageDto;
import com.ureca.ocean.jjh.storeUsage.service.StoreUsageService;
import com.ureca.ocean.jjh.user.dto.UserRankDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Store, Brand API", description = "제휴처 및 혜택 관련 API")
@RequestMapping("api/map")
@RestController @RequiredArgsConstructor @Slf4j
public class StoreController {
    private final StoreService storeService;
    private final StoreUsageService storeUsageService;
    private final GeminiService geminiService;
    private final GoogleVisionOcrService googleVisionOcrService;

    @Operation(summary = "제휴 브랜드 목록 조회", description = "[개발완료] 제휴 브랜드 목록을 필터링(이름 검색, 정렬)하여 가져온다.")
    @GetMapping("/brand")
    public ResponseEntity<BaseResponseDto<?>> getBrands(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortBy", defaultValue = "asc") String sortBy
    ) {
        log.info("제휴 브랜드 목록 조회");
        List<BrandDto> brands = storeService.getBrands(keyword, sortBy);
        return ResponseEntity.ok(BaseResponseDto.success(brands));
    }

    @Operation(summary = "제휴처 목록 조회", description = "[개발완료] 제휴처 목록을 필터링하여 가져온다.")
    @GetMapping("/store")
    public ResponseEntity<BaseResponseDto<?>> getStores(
    		@RequestParam(name = "keyword", required = false) String keyword,
    		@RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "benefit", required = false) String benefit,
    		@RequestParam(name = "latMin", required = false) Double latMin,
    		@RequestParam(name = "latMax", required = false) Double latMax,
    		@RequestParam(name = "lngMin", required = false) Double lngMin,
    		@RequestParam(name = "lngMax", required = false) Double lngMax,
    		@RequestParam(name = "centerLat", required = false) Double centerLat,
    		@RequestParam(name = "centerLng", required = false) Double centerLng

    ) {
        log.info("제휴처 목록 조회");
        List<StoreDto> result = storeService.getStores(keyword, category, benefit, latMin, latMax, lngMin, lngMax, centerLat, centerLng);
        return ResponseEntity.ok(BaseResponseDto.success(result));
    }

    @Operation(summary = "제휴처 목록 조회2", description = "제휴처 목록을 필터링하여 가져온다. (위치 제외)")
    @GetMapping("/stores")
    public ResponseEntity<BaseResponseDto<?>> getStores2(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "benefit", required = false) String benefit
    ) {
        log.info("제휴처 목록 조회2");
        List<StoresDto> result = storeService.getStore(keyword, category, benefit);
        return ResponseEntity.ok(BaseResponseDto.success(result));
    }

    @Operation(summary = "제휴처 상세 조회", description = "[개발완료] 제휴처의 ID로 상세 정보를 조회한다.")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<BaseResponseDto<?>> getStoreDetail(
            @PathVariable(name = "storeId") UUID storeId
    ) {
        StoreDetailDto store = storeService.getDetailStore(storeId);
        log.info("제휴처 상세 조회");
        return ResponseEntity.ok(BaseResponseDto.success(store));
    }

    @Operation(summary = "브랜드 별 혜택 조회", description = "[개발완료] brandID를 이용하여 혜택을 가져온다.")
    @GetMapping("/benefit/{brandId}")
    public ResponseEntity<BaseResponseDto<?>> getBenefits(
            @PathVariable(name = "brandId") UUID brandId
    ) {
        List<BenefitDto> benefits = storeService.getBenefitsByPartnerBrandId(brandId);
        log.info("브랜드 별 혜택 조회");
        return ResponseEntity.ok(BaseResponseDto.success(benefits));
    }

    @Operation(summary = "내 혜택 사용 내역 조회(사용자 도감 조회)", description = "[개발완료] userID와 storeId(nullable)를 이용하여 제휴처 이용내역을 가져온다.")
    @GetMapping("/usage")
    public ResponseEntity<BaseResponseDto<?>> getMyUsages(
            @Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @RequestParam(name = "storeId", required = false) UUID storeId
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        List<StoreUsageDto> storeUsages = storeService.getMyStoreUsage(email, storeId);
        log.info("내 혜택 사용 내역 조회");
        return ResponseEntity.ok(BaseResponseDto.success(storeUsages));
    }

    @Operation(summary = "혜택 인증(OCR)", description = "[개발완료] 영수증 사진을 올려(form_date) 문자로 변환하고 확인한다 / 프론트에서 data만 추출해서 ocr/save로 보내주세요")
    @PostMapping("/ocr")
    public ResponseEntity<BaseResponseDto<?>> getOcr(
            @RequestParam(name = "imageFile") MultipartFile imageFile,
            @RequestHeader("X-User-email") String encodedEmail
    ){
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        String ocrText = googleVisionOcrService.getOcr(imageFile);
        OcrGeminiResponseDto ocrJson = geminiService.OcrTextToJson(ocrText, email);

        log.info("OCR 구조화 결과: {}", ocrJson);
        return ResponseEntity.ok(BaseResponseDto.success(ocrJson));
    }

    @Operation(summary = "혜택 인증(OCR) 저장", description = "[개발완료] 사용자가 확인한 OCR 구조화 결과를 저장")
    @PostMapping("/ocr/save")
    public ResponseEntity<BaseResponseDto<?>> saveOcr(
            @RequestHeader("X-User-email") String encodedEmail,
            @RequestBody OcrGeminiResponseDto dto
    ){
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        StoreUsageDto saved = storeUsageService.saveStoreUsage(email, dto);

        log.info("OCR 결과 저장 완료: {}", saved);
        return ResponseEntity.ok(BaseResponseDto.success(saved));
    }

    @GetMapping("/distinctCategory")
    @Operation(summary = "store의 category를 distinct하게 가져온다.")
    public ResponseEntity<BaseResponseDto<?>> getListCategory(){
        return  ResponseEntity.ok(BaseResponseDto.success(storeService.getListCategory()));
    }

    //category를 통해 brand들을 가져오기
    @GetMapping("/brandByCategory")
    @Operation(summary = "카테고리를 통해 브랜드들을 distinct하게 가져온다.")
    public ResponseEntity<BaseResponseDto<?>> getListBrandByCategory(
            @RequestParam(name = "category") String category
    ){
        return  ResponseEntity.ok(BaseResponseDto.success(storeService.getBrandByCategory(category)));
    }

    @GetMapping("/brandNameById")
    @Operation(summary = "id를 이용해 브랜드 정보를 가져오기")
    public ResponseEntity<BaseResponseDto<?>> getBrandById(
            @RequestParam(name = "id") UUID brandId
    ){
        return  ResponseEntity.ok(BaseResponseDto.success(storeService.getBrandById(brandId)));
    }

    @GetMapping("/benefitNameById")
    @Operation(summary = "id를 이용해 혜택 이름을 가져오기")
    public ResponseEntity<BaseResponseDto<?>> getBenefitNameById(
            @RequestParam(name = "id") UUID benefitId
    ){
        return  ResponseEntity.ok(BaseResponseDto.success(storeService.getBenefitNameById(benefitId)));
    }

    @GetMapping("/store/rank")
    @Operation(summary = "제휴처 랭킹", description = "[개발완료] 제휴처 사용자 순으로 rank desc (0인 store는 가져오지 않는다)")
    public ResponseEntity<BaseResponseDto<?>> getStoreRank(){
        List<StoreRankDto> result = storeService.getStoreRankByDesc();
        return ResponseEntity.ok(BaseResponseDto.success(result));
    }

    @GetMapping("/user/rank")
    @Operation(summary = "사용자 랭킹", description = "[개발완료] 사용자 제휴처 이용 순으로 rank desc (0인 user는 가져오지 않는다)")
    public ResponseEntity<BaseResponseDto<?>> getUserRank(){
        List<UserRankDto> result = storeService.getUserRankByDesc();
        return ResponseEntity.ok(BaseResponseDto.success(result));
    }

    /*------------------------------------------------개발용 API------------------------------------------------*/
    @Operation(summary = "(개발용) Health Check API", description = "[개발완료] 서버 연결 상태 검사")
    @GetMapping("/health")
    public String healthCheck(){
        log.info("health checking...");
        return "map health check fine";
    }

    @Operation(summary = "(개발용) 지도 데이터 테스트", description = "[부분개발] 단순 조회")
    @GetMapping("/kakao-map")
    public void map(HttpServletResponse response) throws IOException {
        response.sendRedirect("/static/index.html");
    }

    @Operation(summary = "(개발용) Gemini API 테스트", description = "[개발완료] 채팅")
    @PostMapping("/gemini")
    public String geminiChat(
            @RequestParam String message
    ){
        return geminiService.geminiChat(message);
    }


}