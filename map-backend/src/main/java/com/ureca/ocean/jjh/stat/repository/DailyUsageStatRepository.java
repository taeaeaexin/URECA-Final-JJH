package com.ureca.ocean.jjh.stat.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserDailyUsageStat;


public interface DailyUsageStatRepository extends MongoRepository<UserDailyUsageStat, String> {
	
	Optional<UserDailyUsageStat> findByUserIdAndDate(UUID userId, LocalDate date);
    
	List<UserDailyUsageStat> findByUserIdAndDateBetweenOrderByDateAsc(UUID userId, LocalDate startDate, LocalDate endDate);
}

