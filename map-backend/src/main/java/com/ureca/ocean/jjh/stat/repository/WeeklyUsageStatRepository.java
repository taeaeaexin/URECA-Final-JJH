package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ureca.ocean.jjh.stat.entity.UserWeeklyUsageStat;


public interface WeeklyUsageStatRepository extends MongoRepository<UserWeeklyUsageStat, String>{
	
	Optional<UserWeeklyUsageStat> findByUserIdAndYearAndMonthAndWeek(UUID userId, int year, int month, int week);

	@Query("{ 'userId': ?0, '$or': [ { 'year': { $lt: ?1 } }, { 'year': ?1, 'week': { $lte: ?2 } } ] }")
	List<UserWeeklyUsageStat> findRecentNWeeks(UUID userId, int endYear, int endWeek, Pageable pageable);

}
