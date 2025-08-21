package com.ureca.ocean.jjh.aibackend.suggestion.service;

import java.util.List;

import com.ureca.ocean.jjh.aibackend.suggestion.dto.request.StoreRecommendRequestDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.StoreRecommendDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.StoreRecommendResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.TitleRecommendResponseDto;

public interface RecommendService {
	StoreRecommendResponseDto storeRecommend(String eamil, StoreRecommendRequestDto request);
	List<TitleRecommendResponseDto> titleRecommend(String email);
}
