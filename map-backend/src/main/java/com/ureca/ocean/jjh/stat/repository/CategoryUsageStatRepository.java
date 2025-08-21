package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserCategoryUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface CategoryUsageStatRepository extends MongoRepository<UserCategoryUsageStat, String>{
	
	Optional<UserCategoryUsageStat> findByUserIdAndPeriodAndCategory(UUID userId, Period period, String category);
	
	List<UserCategoryUsageStat> findByUserIdAndPeriodOrderByCountDesc(UUID userId, Period period);
}
