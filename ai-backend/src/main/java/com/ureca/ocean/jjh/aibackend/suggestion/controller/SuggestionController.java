package com.ureca.ocean.jjh.aibackend.suggestion.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.ocean.jjh.aibackend.common.BaseResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.request.StoreRecommendRequestDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.StoreRecommendResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.TitleRecommendResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.service.RecommendService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "AI API", description = "AI 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Slf4j
public class SuggestionController {
	
	private final RecommendService recommendService;
	
	@Operation(summary = "제휴처 추천", description = "사용자의 정보를 토대로 제휴처를 하나 추천한다.")
	@PostMapping("/recommend/store")
	public ResponseEntity<BaseResponseDto<StoreRecommendResponseDto>> storeRecommend(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestBody StoreRecommendRequestDto request){
		String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
		return ResponseEntity.ok(BaseResponseDto.success(recommendService.storeRecommend(email, request)));
	}
	
	@Operation(summary = "칭호 생성", description = "사용자의 정보를 토대로 칭호를 3가지 제시한다.")
	@GetMapping("/recommend/title")
	public ResponseEntity<BaseResponseDto<List<TitleRecommendResponseDto>>> titleRecommend(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail){
		String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
		List<TitleRecommendResponseDto> result = recommendService.titleRecommend(email);
		return ResponseEntity.ok(BaseResponseDto.success(result));
	}
}
