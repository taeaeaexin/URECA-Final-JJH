package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserBrandUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface BrandUsageStatRepository extends MongoRepository<UserBrandUsageStat, String>{

	List<UserBrandUsageStat> findTop3ByUserIdAndPeriodOrderByCountDesc(UUID userId, Period period);
    Optional<UserBrandUsageStat> findByUserIdAndBrandNameAndPeriod(UUID userId, String brandName, Period period);

}
