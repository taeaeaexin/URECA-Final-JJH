package com.ureca.ocean.jjh.stat.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.stat.dto.DailyAmountDto;
import com.ureca.ocean.jjh.stat.dto.MonthlyAmountDto;
import com.ureca.ocean.jjh.stat.dto.StatsDataDto;
import com.ureca.ocean.jjh.stat.dto.UsageCountDto;
import com.ureca.ocean.jjh.stat.dto.WeeklyAmountDto;
import com.ureca.ocean.jjh.stat.entity.enums.Period;
import com.ureca.ocean.jjh.stat.service.UserStatService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Statistic API", description = "사용자 통계 조회 API")
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class UserStatController {

	private final UserStatService userStatService;

	private String decodeEmail(String encodedEmail) {
		return URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
	}

	@Operation(summary = "누적 절약액 및 총 이용 통계 조회", description = "[개발완료] 사용자의 총 이용 횟수 및 절약 금액을 반환한다.")
	@GetMapping("/stat/savings")
	public ResponseEntity<BaseResponseDto<StatsDataDto>> getSavings(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
		String email = decodeEmail(encodedEmail);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getSavings(email)));
	}

	@Operation(summary = "일별 통계 조회", description = "[개발완료] 최근 N일 간의 절약액 및 이용 횟수를 일자별로 조회한다.")
	@GetMapping("/stat/daily")
	public ResponseEntity<BaseResponseDto<List<DailyAmountDto>>> getDaily(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "num", defaultValue = "10") int num) {
		String email = decodeEmail(encodedEmail);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getDailyUsage(email, num)));
	}

	@Operation(summary = "주별 통계 조회", description = "[개발완료] 최근 N주 간의 절약액 및 이용 횟수를 주차별로 조회한다.")
	@GetMapping("/stat/weekly")
	public ResponseEntity<BaseResponseDto<List<WeeklyAmountDto>>> getWeekly(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "num", defaultValue = "10") int num) {
		String email = decodeEmail(encodedEmail);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getWeeklyUsage(email, num)));
	}

	@Operation(summary = "월별 통계 조회", description = "[개발완료] 최근 N개월 간의 절약액 및 이용 횟수를 월별로 조회한다.")
	@GetMapping("/stat/monthly")
	public ResponseEntity<BaseResponseDto<List<MonthlyAmountDto>>> getMonthly(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "num", defaultValue = "5") int num) {
		String email = decodeEmail(encodedEmail);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getMonthlyUsage(email, num)));
	}

	@Operation(summary = "카테고리별 통계 조회", description = "[개발완료] 선택된 기간 동안의 제휴 카테고리별 이용 횟수를 조회한다.")
	@GetMapping("/stat/category")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getCategory(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getCategoryUsage(email, period)));
	}

	@Operation(summary = "지역별 통계 조회", description = "[개발완료] 선택된 기간 동안의 지역(행정구역)별 이용 횟수를 조회한다.")
	@GetMapping("/stat/region")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getRegion(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getRegionUsage(email, period)));
	}

	@Operation(summary = "요일별 통계 조회", description = "[개발완료] 선택된 기간 동안의 요일(Mon~Sun)별 이용 횟수를 조회한다.")
	@GetMapping("/stat/weekday")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getWeekday(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getWeekdayUsage(email, period)));
	}

	@Operation(summary = "시간대별 통계 조회", description = "[개발완료] 선택된 기간 동안의 시간대(0~3시 등)별 이용 횟수를 조회한다.")
	@GetMapping("/stat/hourly")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getHourly(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getHourlyUsage(email, period)));
	}

	@Operation(summary = "브랜드별 통계 조회", description = "[개발완료] 선택된 기간 동안의 브랜드별 이용 횟수를 조회한다.")
	@GetMapping("/stat/brand")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getBrand(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getBrandUsage(email, period)));
	}

	@Operation(summary = "제휴처별 통계 조회", description = "[개발완료] 선택된 기간 동안의 제휴 매장별 이용 횟수를 조회한다.")
	@GetMapping("/stat/store")
	public ResponseEntity<BaseResponseDto<List<UsageCountDto>>> getStore(
			@Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
			@RequestParam(name = "period") String periodStr) {
		String email = decodeEmail(encodedEmail);
		Period period = Period.from(periodStr);
		return ResponseEntity.ok(BaseResponseDto.success(userStatService.getStoreUsage(email, period)));
	}
}

