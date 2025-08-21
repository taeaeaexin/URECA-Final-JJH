package com.ureca.ocean.jjh.aibackend.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ureca.ocean.jjh.aibackend.client.dto.StoreDto;
import com.ureca.ocean.jjh.aibackend.client.dto.StoreUsageDto;
import com.ureca.ocean.jjh.aibackend.common.BaseResponseDto;
import com.ureca.ocean.jjh.aibackend.common.constant.DomainConstant;
import com.ureca.ocean.jjh.aibackend.common.exception.AiException;
import com.ureca.ocean.jjh.aibackend.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreClient {

	private final RestTemplate restTemplate;
	private String mapUrl = DomainConstant.MAP_URL;
	
	public List<StoreDto> getStores(String keyword, String category,
            						Double latMin, Double latMax,
            						Double lngMin, Double lngMax,
            						Double centerLat, Double centerLng){
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mapUrl + "api/map/store");
		
		if (keyword != null) builder.queryParam("keyword", keyword);
        if (category != null) builder.queryParam("category", category);
        if (latMin != null) builder.queryParam("latMin", latMin);
        if (latMax != null) builder.queryParam("latMax", latMax);
        if (lngMin != null) builder.queryParam("lngMin", lngMin);
        if (lngMax != null) builder.queryParam("lngMax", lngMax);
        if (centerLat != null) builder.queryParam("centerLat", centerLat);
        if (centerLng != null) builder.queryParam("centerLng", centerLng);
        
        String url = builder.toUriString();
        
        ResponseEntity<BaseResponseDto<List<StoreDto>>> response = restTemplate.exchange(
        		url,
        		HttpMethod.GET,
        		null,
        		new ParameterizedTypeReference<>() {}
        );
        BaseResponseDto<List<StoreDto>> body = response.getBody();
        if (body == null) {
            throw new AiException(ErrorCode.CLIENT_CALL_ERROR);
        }
        return body.getData();
	}
	
	public StoreDto getStoreDetail(UUID storeId) {
		String url = mapUrl + "api/map/store/" + storeId;
		ResponseEntity<BaseResponseDto<StoreDto>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {}
		);
		BaseResponseDto<StoreDto> body = response.getBody();
        if (body == null) {
            throw new AiException(ErrorCode.CLIENT_CALL_ERROR);
        }
        return body.getData();
	}
	
	public List<StoreDto> getAllBookmarks(String email){
		
		String url = mapUrl + "api/map/bookmark";
		String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-User-email", encodedEmail);
		
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<BaseResponseDto<List<StoreDto>>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				new ParameterizedTypeReference<>() {}
		);
		
		BaseResponseDto<List<StoreDto>> body = response.getBody();
        if (body == null) {
            throw new AiException(ErrorCode.CLIENT_CALL_ERROR);
        }
        return body.getData();
	}
	
	public List<StoreUsageDto> getAllUsages(String email){
		
		String url = mapUrl + "api/map/usage";
		String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-User-email", encodedEmail);
		
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<BaseResponseDto<List<StoreUsageDto>>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				new ParameterizedTypeReference<>() {}
		);
		
		BaseResponseDto<List<StoreUsageDto>> body = response.getBody();
        if (body == null) {
            throw new AiException(ErrorCode.CLIENT_CALL_ERROR);
        }
        return body.getData();
	}
}
