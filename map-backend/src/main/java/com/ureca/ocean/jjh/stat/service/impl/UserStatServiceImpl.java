package com.ureca.ocean.jjh.stat.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ureca.ocean.jjh.stat.dto.DailyAmountDto;
import com.ureca.ocean.jjh.stat.dto.MonthlyAmountDto;
import com.ureca.ocean.jjh.stat.dto.StatsDataDto;
import com.ureca.ocean.jjh.stat.dto.UsageCountDto;
import com.ureca.ocean.jjh.stat.dto.WeeklyAmountDto;
import com.ureca.ocean.jjh.stat.entity.UserCategoryUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserDailyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserHourlyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserMonthlyUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserRegionUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserWeekdayUsageStat;
import com.ureca.ocean.jjh.stat.entity.UserWeeklyUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;
import com.ureca.ocean.jjh.stat.repository.BrandUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.CategoryUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.DailyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.HourlyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.MonthlyUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.RegionUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.StoreUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.UsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.WeekdayUsageStatRepository;
import com.ureca.ocean.jjh.stat.repository.WeeklyUsageStatRepository;
import com.ureca.ocean.jjh.stat.service.UserStatService;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStatServiceImpl implements UserStatService{

	private final UsageStatRepository usageStatRepository;
	private final DailyUsageStatRepository dailyUsageStatRepository;
	private final WeeklyUsageStatRepository weeklyUsageStatRepository;
	private final MonthlyUsageStatRepository monthlyUsageStatRepository;
	private final CategoryUsageStatRepository categoryUsageStatRepository;
	private final RegionUsageStatRepository regionUsageStatRepository;
	private final WeekdayUsageStatRepository weekdayUsageStatRepository;
	private final HourlyUsageStatRepository hourlyUsageStatRepository;
	private final BrandUsageStatRepository brandUsageStatRepository;
	private final StoreUsageStatRepository storeUsageStatRepository;
	private final UserClient userClient;
	
	@Override
	public StatsDataDto getSavings(String email) {

	    UUID userId = getUserId(email);

	    Optional<UserUsageStat> optionalMyStat = usageStatRepository.findByUserId(userId);
	    List<UserUsageStat> allStats = usageStatRepository.findAll();

	    int totalCount = allStats.stream().mapToInt(UserUsageStat::getCount).sum();
	    int totalAmount = allStats.stream().mapToInt(UserUsageStat::getAmount).sum();
	    int userCount = allStats.size();

	    int avgCount = userCount > 0 ? totalCount / userCount : 0;
	    int avgAmount = userCount > 0 ? totalAmount / userCount : 0;

	    int myCount = optionalMyStat.map(UserUsageStat::getCount).orElse(0);
	    int myAmount = optionalMyStat.map(UserUsageStat::getAmount).orElse(0);

	    return StatsDataDto.builder()
	        .usage(new StatsDataDto.StatPair(myCount, avgCount))
	        .savings(new StatsDataDto.StatPair(myAmount, avgAmount))
	        .build();
	}



	@Override
	public List<DailyAmountDto> getDailyUsage(String email, int num) {

		UUID userId = getUserId(email);
	    
	    LocalDate endDate = LocalDate.now().minusDays(1);
	    LocalDate startDate = endDate.minusDays(num - 1);

	    List<UserDailyUsageStat> stats = dailyUsageStatRepository
	            .findByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);
	    
	    Map<LocalDate, Integer> amountMap = stats.stream()
	            .collect(Collectors.toMap(UserDailyUsageStat::getDate, UserDailyUsageStat::getAmount));
	        
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
	    
	    return startDate.datesUntil(endDate.plusDays(1)) // inclusive
	            .map(date -> new DailyAmountDto(
	                date.format(formatter),
	                amountMap.getOrDefault(date, 0)
	            ))
	            .toList();
	}

	@Override
	public List<WeeklyAmountDto> getWeeklyUsage(String email, int num) {

		UUID userId = getUserId(email);
	    
	    LocalDate now = LocalDate.now();
	    LocalDate endDate = now.minusWeeks(1);
	    int endWeek = endDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	    int endYear = endDate.get(IsoFields.WEEK_BASED_YEAR);
	    
	    Pageable pageable = PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "year", "week"));
	    
	    List<UserWeeklyUsageStat> stats = weeklyUsageStatRepository
	    		.findRecentNWeeks(userId, endYear, endWeek, pageable);
	    
	    stats.sort(Comparator.comparing(UserWeeklyUsageStat::getYear)
	    	              	 .thenComparing(UserWeeklyUsageStat::getWeek));
	    
	    Map<String, Integer> amountMap = stats.stream().collect(Collectors.toMap(
	            stat -> stat.getYear() + "-" + stat.getMonth() + "-" + stat.getWeek(),
	            UserWeeklyUsageStat::getAmount
	    ));

	    List<WeeklyAmountDto> result = new ArrayList<>();

	    for (int i = num - 1; i >= 0; i--) {
	        LocalDate weekDate = endDate.minusWeeks(i);

	        int year = weekDate.getYear();
	        int month = weekDate.getMonthValue();
	        int weekOfMonth = getWeekOfMonth(weekDate);

	        String key = year + "-" + month + "-" + weekOfMonth;
	        int amount = amountMap.getOrDefault(key, 0);

	        String label = String.format("%d월 %d주차", month, weekOfMonth);
	        result.add(new WeeklyAmountDto(label, amount));
	    }

	    return result;
	}
	
	public static int getWeekOfMonth(LocalDate date) {
	    LocalDate firstDayOfMonth = date.withDayOfMonth(1);
	    int firstDayWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
	    int day = date.getDayOfMonth();

	    // 요일 시작 보정 (월요일 기준)
	    int adjustedDay = day + (firstDayWeek - 1);
	    return (adjustedDay - 1) / 7 + 1;
	}


	@Override
	public List<MonthlyAmountDto> getMonthlyUsage(String email, int num) {

		UUID userId = getUserId(email);
	    
	    YearMonth thisMonth = YearMonth.now();
	    YearMonth endMonth = thisMonth.minusMonths(1);
	    
	    Pageable pageable = PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "year", "week"));
	    
	    List<UserMonthlyUsageStat> stats = monthlyUsageStatRepository
	            .findRecentNMonths(userId, endMonth.getYear(), endMonth.getMonthValue(), pageable);
	    
	    Map<String, Integer> amountMap = stats.stream().collect(Collectors.toMap(
	            stat -> String.format("%04d-%02d", stat.getYear(), stat.getMonth()),
	            UserMonthlyUsageStat::getAmount
	    ));
	    
	    List<MonthlyAmountDto> result = new ArrayList<>();

	    for (int i = num - 1; i >= 0; i--) {
	        YearMonth targetMonth = endMonth.minusMonths(i);
	        String key = String.format("%04d-%02d", targetMonth.getYear(), targetMonth.getMonthValue());
	        int amount = amountMap.getOrDefault(key, 0);
	        result.add(new MonthlyAmountDto(targetMonth.getMonthValue() + "월", amount));
	    }

	    return result;
	}

	@Override
	public List<UsageCountDto> getCategoryUsage(String email, Period period) {

		UUID userId = getUserId(email);
	    
	    List<UserCategoryUsageStat> stats = categoryUsageStatRepository
	    		.findByUserIdAndPeriodOrderByCountDesc(userId, period);
	    
	    return stats.stream()
	            .map(stat -> new UsageCountDto(stat.getCategory(), stat.getCount()))
	            .toList();
	}

	@Override
	public List<UsageCountDto> getRegionUsage(String email, Period period) {

		UUID userId = getUserId(email);
	    
	    List<UserRegionUsageStat> stats = regionUsageStatRepository
	    		.findByUserIdAndPeriodOrderByCountDesc(userId, period);
	    
		return stats.stream()
				.map(stat -> new UsageCountDto(stat.getRegion(), stat.getCount()))
				.toList();
	}

	@Override
	public List<UsageCountDto> getWeekdayUsage(String email, Period period) {

		UUID userId = getUserId(email);

	    List<UserWeekdayUsageStat> stats = weekdayUsageStatRepository
	    		.findByUserIdAndPeriod(userId, period);
	    
	    Map<String, Integer> countMap = stats.stream()
	    		.collect(Collectors.toMap(UserWeekdayUsageStat::getWeekdayName, UserWeekdayUsageStat::getCount));
	    
	    List<String> weekdays = List.of("월", "화", "수", "목", "금", "토", "일");
	    
	    
	    return weekdays.stream()
	            .map(day -> new UsageCountDto(day, countMap.getOrDefault(day, 0)))
	            .toList();
	}

	@Override
	public List<UsageCountDto> getHourlyUsage(String email, Period period) {
		
	    UUID userId = getUserId(email);

	    List<UserHourlyUsageStat> stats = hourlyUsageStatRepository
	    		.findByUserIdAndPeriod(userId, period);
	    
	    Map<String, Integer> countMap = stats.stream()
	            .collect(Collectors.toMap(UserHourlyUsageStat::getHourRange, UserHourlyUsageStat::getCount));

	    // 전체 시간대 정의
	    List<String> hourRanges = List.of(
	        "0~3시", "3~6시", "6~9시", "9~12시",
	        "12~15시", "15~18시", "18~21시", "21~24시"
	    );

	    // 누락된 시간대는 count 0으로 채움
	    return hourRanges.stream()
	            .map(hour -> new UsageCountDto(hour, countMap.getOrDefault(hour, 0)))
	            .toList();
	}

	@Override
	public List<UsageCountDto> getBrandUsage(String email, Period period) {
		
		UUID userId = getUserId(email);

	    return brandUsageStatRepository
	            .findTop3ByUserIdAndPeriodOrderByCountDesc(userId, period)
	            .stream()
	            .map(stat -> new UsageCountDto(stat.getBrandName(), stat.getCount()))
	            .toList();
	}

	@Override
	public List<UsageCountDto> getStoreUsage(String email, Period period) {

		UUID userId = getUserId(email);

	    return storeUsageStatRepository
	            .findTop3ByUserIdAndPeriodOrderByCountDesc(userId, period)
	            .stream()
	            .map(stat -> new UsageCountDto(stat.getStoreName(), stat.getCount()))
	            .toList();
	}

	private UUID getUserId(String email) {
		UserDto user = userClient.getUserByEmail(email);
		return user.getId();
	}

}
