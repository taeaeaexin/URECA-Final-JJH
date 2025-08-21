package com.ureca.ocean.jjh.bookmarks.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.ocean.jjh.bookmarks.service.BookmarkService;
import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.store.dto.StoreDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Bookmark API", description = "즐겨찾기 관련 API")
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkService bookmarkService;
	
	@Operation(summary = "즐겨찾기 목록 조회", description = "사용자 ID와 카테고리 키워드를 통해 즐겨찾기 한 제휴처 목록을 조회한다. 카테고리가 없다면 모든 즐겨찾기 목록을 조회한다.")
	@GetMapping("/bookmark")
	public ResponseEntity<BaseResponseDto<List<StoreDto>>> getBookmarks(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "category", required = false) List<String> categories){
		String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
		List<StoreDto> bookmarks = bookmarkService.getBookmarksByUserId(email, categories);
		return ResponseEntity.ok(BaseResponseDto.success(bookmarks));
	}
	
	@Operation(summary = "즐겨찾기 등록", description = "제휴처를 사용자 즐겨찾기에 등록한다.")
	@PostMapping("/bookmark/{storeId}")
	public ResponseEntity<BaseResponseDto<String>> addBookmark(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@PathVariable(name = "storeId") UUID storeId){
		String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
		bookmarkService.addBookmark(email, storeId);
		return ResponseEntity.ok(BaseResponseDto.success("즐겨찾기 등록 완료"));
	}
	
	@Operation(summary = "즐겨찾기 삭제", description = "제휴처를 사용자 즐겨찾기에서 삭제한다.")
	@DeleteMapping("/bookmark/{storeId}")
	public ResponseEntity<BaseResponseDto<String>> deleteBookmark(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@PathVariable(name = "storeId") UUID storeId){
		String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
		bookmarkService.deleteBookmark(email, storeId);
		return ResponseEntity.ok(BaseResponseDto.success("즐겨찾기 삭제 완료"));
	}
}
