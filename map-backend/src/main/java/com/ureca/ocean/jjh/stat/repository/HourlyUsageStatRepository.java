package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserHourlyUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface HourlyUsageStatRepository extends MongoRepository<UserHourlyUsageStat, String>{
	
	List<UserHourlyUsageStat> findByUserIdAndPeriod(UUID userId, Period period);
	Optional<UserHourlyUsageStat> findByUserIdAndHourRangeAndPeriod(UUID userId, String hourRange, Period period);
}
