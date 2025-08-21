package com.ureca.ocean.jjh.stat.service;

import java.util.List;

import com.ureca.ocean.jjh.stat.dto.DailyAmountDto;
import com.ureca.ocean.jjh.stat.dto.MonthlyAmountDto;
import com.ureca.ocean.jjh.stat.dto.StatsDataDto;
import com.ureca.ocean.jjh.stat.dto.UsageCountDto;
import com.ureca.ocean.jjh.stat.dto.WeeklyAmountDto;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface UserStatService {

	StatsDataDto getSavings(String email);
	
	List<DailyAmountDto> getDailyUsage(String email, int num);
	List<WeeklyAmountDto> getWeeklyUsage(String email, int num);
	List<MonthlyAmountDto> getMonthlyUsage(String email, int num);
	
	List<UsageCountDto> getCategoryUsage(String email, Period period);
	List<UsageCountDto> getRegionUsage(String email, Period period);
	List<UsageCountDto> getWeekdayUsage(String email, Period period);
	List<UsageCountDto> getHourlyUsage(String email, Period period);
	List<UsageCountDto> getBrandUsage(String email, Period period);
	List<UsageCountDto> getStoreUsage(String email, Period period);
	
}
