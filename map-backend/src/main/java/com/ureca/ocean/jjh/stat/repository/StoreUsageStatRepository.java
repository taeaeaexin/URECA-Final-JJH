package com.ureca.ocean.jjh.stat.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserStoreUsageStat;
import com.ureca.ocean.jjh.stat.entity.enums.Period;

public interface StoreUsageStatRepository extends MongoRepository<UserStoreUsageStat, String>{

	List<UserStoreUsageStat> findTop3ByUserIdAndPeriodOrderByCountDesc(UUID userId, Period period);
	Optional<UserStoreUsageStat> findByUserIdAndStoreNameAndPeriod(UUID userId, String storeName, Period period);

}
