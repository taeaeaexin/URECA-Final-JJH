package com.ureca.ocean.jjh.aibackend.suggestion.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.ocean.jjh.aibackend.client.StoreClient;
import com.ureca.ocean.jjh.aibackend.client.UserClient;
import com.ureca.ocean.jjh.aibackend.client.dto.StoreDto;
import com.ureca.ocean.jjh.aibackend.client.dto.StoreUsageDto;
import com.ureca.ocean.jjh.aibackend.client.dto.UserDto;
import com.ureca.ocean.jjh.aibackend.common.exception.AiException;
import com.ureca.ocean.jjh.aibackend.common.exception.ErrorCode;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.request.StoreRecommendRequestDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.StoreRecommendDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.StoreRecommendResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.dto.response.TitleRecommendResponseDto;
import com.ureca.ocean.jjh.aibackend.suggestion.service.RecommendService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceImpl implements RecommendService{

	private final ChatClient chatClient;
	private final UserClient userClient;
	private final StoreClient storeClient;

	@Override
	public StoreRecommendResponseDto storeRecommend(String email, StoreRecommendRequestDto request) {

		List<StoreDto> storeList = storeClient.getStores(request.getKeyword(), request.getCategory(),
														 request.getLatMin(), request.getLatMax(), 
														 request.getLngMin(), request.getLngMax(), 
														 request.getCenterLat(), request.getCenterLng());
		
		String storeListString = storeList.stream()
			    .map(store -> String.format("""
			        {
			          "id": %s,
			          "name": "%s",
			          "category": "%s",
			          "latitude": %.6f,
			          "longitude": %.6f,
			        }
			        """,
			        store.getId(),
			        store.getName(),
					store.getCategory(),
					store.getLatitude(),
					store.getLongitude())
				)
			    .collect(Collectors.joining(",\n", "[\n", "\n]"));
		
		String userInfo = getUserInfo(email);
		String prompt = String.format("""
				당신은 개인화된 매장 추천을 수행하는 AI 시스템입니다.

				[사용자 정보]
				%s
				
				[추천 대상 매장 목록]
				%s
				
				당신의 역할은 사용자 정보와 매장 목록을 바탕으로 **사용자에게 가장 적합한 매장 하나를 엄선하여 추천**하는 것입니다.
				추천 시에는 사용자의 위치, 즐겨찾기, 방문 이력, 관심 카테고리 등을 고려하세요.
				추천 이유는 **사용자와 매장의 연결 고리를 구체적으로** 설명해야 하며, 단순한 나열이나 일반적인 설명은 피해야 합니다.
				
				📌 응답은 반드시 중괄호로 시작하여 중괄호로 끝나는 **아래 형식만**으로 출력해야 하며, 예시는 아래와 같다.
				
				{
				  "storeId": "UUID 형태의 storeId",
				  "reason": "이 매장을 추천하는 구체적이고 설득력 있는 이유"
				}
				""",
				storeListString,
				userInfo
				);
		
		String guideLine = """
				당신은 사용자 맞춤형 제휴처를 정확히 선별하고 추천하는 AI입니다.

				당신의 임무는 사용자의 취향, 이용 기록, 현재 위치 등을 종합적으로 분석하여 수많은 매장 중에서 가장 이상적인 매장 하나를 찾아주는 것입니다.
				사용자에게 실질적인 가치를 제공하는 추천이 되도록 하며, 반드시 지정된 포맷만 출력해야 합니다.
				""";
		
		String response = chatClient.prompt()
								.system(guideLine)
								.user(prompt)
								.call()
								.content();
		
		log.warn("🔍 OpenAI 응답: {}", response);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			StoreRecommendDto storeRecommend = objectMapper.readValue(response, StoreRecommendDto.class);
			
			StoreDto store = storeClient.getStoreDetail(storeRecommend.getStoreId());
			String reason = storeRecommend.getReason();
			return new StoreRecommendResponseDto(store, reason);
		}catch (IOException e) {
			throw new AiException(ErrorCode.PARSING_ERROR);
		}
	}

	@Override
	public List<TitleRecommendResponseDto> titleRecommend(String email) {
		
		UserDto user = userClient.getUserByEmail(email);
		log.info("사용자 이름: {}", user.getName());
		
		String userInfo = getUserInfo(email);
		
		String prompt = String.format("""
				당신은 사용자의 데이터를 분석하여 ✨개성 넘치고 감각적인 칭호(title)✨를 부여하는 AI입니다.

				🔎 역할:
				브랜드 선호도, 방문 이력, 즐겨찾기 내역 등을 바탕으로, 사용자의 성향을 창의적으로 해석하여 딱 어울리는 칭호 3가지를 지어주세요.

				🎯 반드시 다음 기준을 따르세요:
				- 칭호는 **재미** 또는 **감성**, 또는 둘 다를 담아야 합니다.
				- 단순하고 밋밋한 표현(예: “카페 애호가”) 대신, 더 구체적이고 생생한 언어를 사용하세요.
				- 숫자(예: 3회 이상 방문), 밈, 이모지(🧁, 🎬, ☕️ 등)는 자연스럽게 사용할 수 있습니다.
				- 각 칭호에는 짧고 명확한 이유(reason)를 포함해야 하며, 문장은 반드시 **“~로 확인돼요.”** 로 끝맺으세요.
				- 이유는 실제 사용자의 데이터를 기반으로 사실처럼 들리게 구성하세요.

				📦 출력은 무조건 아래 구조를 따라야 합니다:
				(⚠️ “```” 또는 “JSON” 같은 포맷 설명은 넣지 마세요)

				[
				  { "title": "쿠폰 장인 🏷️", "reason": "혜택을 자주 사용한 것으로 확인돼요." },
				  { "title": "투썸의 사나이 ☕", "reason": "최근 투썸플레이스를 5회 이상 방문한 것으로 확인돼요." },
				  { "title": "적립의 요정 🧚", "reason": "즐겨찾기 브랜드를 꾸준히 방문한 것으로 확인돼요." }
				]

				📌 포인트:
				- 모든 문장은 자연스럽고 사람 말을 닮아야 해요.
				- 응답에는 반드시 JSON 배열 **형태만** 포함되어야 하며, 설명 텍스트나 추가 코멘트는 넣지 마세요.

				👤 사용자 정보:
				%s
				""", userInfo);



		
		String guideline = """
				- 당신의 임무는 사용자의 이용 데이터를 바탕으로 성향에 맞는 칭호를 정확히 부여하는 것입니다.
				- 칭호는 정보 전달뿐만 아니라 사용자에게 즐거움을 줄 수 있어야 합니다.
				- 필요 시 2030 세대가 좋아할 만한 트렌디한 키워드나 표현을 활용해도 됩니다.
				- 단, 과도한 유머나 비속어, 무의미한 밈 표현은 금지합니다.
				""";


		
		String response = chatClient.prompt()
								.system(guideline)
								.user(prompt)
								.call()
								.content();
		
		log.warn("🔍 OpenAI 응답: {}", response);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
            TitleRecommendResponseDto[] array = objectMapper.readValue(response, TitleRecommendResponseDto[].class);
            return Arrays.asList(array);
		} catch(IOException e){
			throw new AiException(ErrorCode.PARSING_ERROR);
		}
	}
	
	
	private String getUserInfo(String email) {
	    List<StoreDto> bookmarks = storeClient.getAllBookmarks(email);
	    List<StoreUsageDto> usages = storeClient.getAllUsages(email);

	    // 즐겨찾기 브랜드 (중복 제거 + 순서 유지)
	    Set<String> favoriteBrands = bookmarks.stream()
	            .map(StoreDto::getBrandName)
	            .filter(Objects::nonNull)
	            .collect(Collectors.toCollection(LinkedHashSet::new));

	    // storeId → StoreDto 매핑
	    Map<UUID, StoreDto> storeDtoMap = bookmarks.stream()
	            .collect(Collectors.toMap(StoreDto::getId, Function.identity()));

	    // 최근 이용 기록을 브랜드별로 그룹화
	    Map<String, List<StoreUsageDto>> usageByBrand = usages.stream()
	            .sorted(Comparator.comparing(StoreUsageDto::getVisitedAt).reversed()) // 최근 방문순
	            .filter(usage -> storeDtoMap.containsKey(usage.getStoreId())) // 대응되는 매장만
	            .collect(Collectors.groupingBy(usage -> {
	                StoreDto store = storeDtoMap.get(usage.getStoreId());
	                return store.getBrandName();
	            }, LinkedHashMap::new, Collectors.toList()));

	    StringBuilder recentUsageBuilder = new StringBuilder();

	    if (usageByBrand.isEmpty()) {
	        recentUsageBuilder.append("없음");
	    } else {
	        for (Map.Entry<String, List<StoreUsageDto>> entry : usageByBrand.entrySet()) {
	            String brand = entry.getKey();
	            List<StoreUsageDto> usageList = entry.getValue();
	            int count = usageList.size();
	            String lastVisited = usageList.get(0).getVisitedAt().toString(); // 가장 최근 방문

	            recentUsageBuilder.append(String.format("- %s: %d회 방문 (최근: %s)\n", brand, count, lastVisited));
	        }
	    }

	    String favoritesFormatted = favoriteBrands.isEmpty()
	            ? "없음"
	            : String.join(", ", favoriteBrands);

	    return String.format("""
	            👤 사용자 이용 정보 요약

	            🔖 즐겨찾는 브랜드:
	            %s

	            🕘 최근 이용 브랜드:
	            %s
	            """, favoritesFormatted, recentUsageBuilder.toString().trim());
	}

	
}
