package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ureca.ocean.jjh.stat.entity.UserMonthlyUsageStat;


public interface MonthlyUsageStatRepository extends MongoRepository<UserMonthlyUsageStat, String>{
	
	
	Optional<UserMonthlyUsageStat> findByUserIdAndYearAndMonth(UUID userId, int year, int month);
	
    @Query("{ 'userId': ?0, '$or': [ { 'year': { $lt: ?1 } }, { 'year': ?1, 'month': { $lte: ?2 } } ] }")
    List<UserMonthlyUsageStat> findRecentNMonths(UUID userId, int endYear, int endMonth, Pageable pageable);

}
