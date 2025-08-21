package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserWeekdayUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface WeekdayUsageStatRepository extends MongoRepository<UserWeekdayUsageStat, String>{
	
	List<UserWeekdayUsageStat> findByUserIdAndPeriod(UUID userId, Period period);
	Optional<UserWeekdayUsageStat> findByUserIdAndWeekdayNameAndPeriod(UUID userId, String weekdayName, Period period);
}
