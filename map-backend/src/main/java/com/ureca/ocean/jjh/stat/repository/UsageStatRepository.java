package com.ureca.ocean.jjh.stat.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ureca.ocean.jjh.stat.entity.UserUsageStat;


public interface UsageStatRepository extends MongoRepository<UserUsageStat, String>{

	Optional<UserUsageStat> findByUserId(UUID userId);
}
