package com.ureca.ocean.jjh.storeUsage.repository;

import java.time.LocalDateTime;

import com.ureca.ocean.jjh.store.entity.Store;
import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ureca.ocean.jjh.batch.dto.BrandUsageDto;
import com.ureca.ocean.jjh.batch.dto.CategoryUsageDto;
import com.ureca.ocean.jjh.batch.dto.DailyUsageDto;
import com.ureca.ocean.jjh.batch.dto.HourlyUsageDto;
import com.ureca.ocean.jjh.batch.dto.MonthlyUsageDto;
import com.ureca.ocean.jjh.batch.dto.RegionUsageDto;
import com.ureca.ocean.jjh.batch.dto.StoreUsageDto;
import com.ureca.ocean.jjh.batch.dto.TotalUsageDto;
import com.ureca.ocean.jjh.batch.dto.WeekdayUsageDto;
import com.ureca.ocean.jjh.batch.dto.WeeklyUsageDto;

public interface StoreUsageRepository extends JpaRepository<StoreUsage, UUID>, StoreUsageRepositoryCustom {
    
	@Query("""
	        SELECT new com.ureca.ocean.jjh.batch.dto.TotalUsageDto(
	            s.userId,
	            COUNT(s) as count,
	            SUM(s.benefitAmount) as amount
	        )
	        FROM StoreUsage s
	        GROUP BY s.userId
	    """)
	    List<TotalUsageDto> summarizeUsageByUser();
	
	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.DailyUsageDto(
		        su.userId, DATE(su.visitedAt), COUNT(su), SUM(su.benefitAmount)
		    )
		    FROM StoreUsage su
		    GROUP BY su.userId, DATE(su.visitedAt)
		""")
		List<DailyUsageDto> summarizeDailyUsageByUser();
	
	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.WeeklyUsageDto(
		        u.userId,
		        FUNCTION('YEAR', u.visitedAt),
		        FUNCTION('MONTH', u.visitedAt),
		        FUNCTION('FLOOR', (FUNCTION('DAY', u.visitedAt) - 1) / 7) + 1,
		        COUNT(u),
		        SUM(u.benefitAmount)
		    )
		    FROM StoreUsage u
		    GROUP BY u.userId, FUNCTION('YEAR', u.visitedAt), FUNCTION('MONTH', u.visitedAt), 
		             FUNCTION('FLOOR', (FUNCTION('DAY', u.visitedAt) - 1) / 7) + 1
		""")
		List<WeeklyUsageDto> summarizeWeeklyUsageByUser();

	
	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.MonthlyUsageDto(
		        su.userId,
		        YEAR(su.visitedAt),
		        MONTH(su.visitedAt),
		        COUNT(su),
		        SUM(su.benefitAmount)
		    )
		    FROM StoreUsage su
		    GROUP BY su.userId, YEAR(su.visitedAt), MONTH(su.visitedAt)
		""")
		List<MonthlyUsageDto> summarizeMonthlyUsageByUser();

	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.CategoryUsageDto(
		        su.userId,
		        su.store.category,
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId, su.store.category
		""")
		List<CategoryUsageDto> summarizeCategoryUsageByUser(
		    @Param("startDate") LocalDateTime startDate
		);
	
	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.RegionUsageDto(
		        su.userId,
		        SUBSTRING(s.address, 1, LOCATE(' ', s.address, LOCATE(' ', s.address) + 1) - 1),
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    JOIN su.store s
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId, SUBSTRING(s.address, 1, LOCATE(' ', s.address, LOCATE(' ', s.address) + 1) - 1)
		""")
		List<RegionUsageDto> summarizeRegionUsageByUser(@Param("startDate") LocalDateTime startDate);

	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.WeekdayUsageDto(
		        su.userId,
		        FUNCTION('dayname', su.visitedAt),
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId, FUNCTION('dayname', su.visitedAt)
		""")
		List<WeekdayUsageDto> summarizeWeekdayUsageByUser(@Param("startDate") LocalDateTime startDate);

	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.HourlyUsageDto(
		        su.userId,
		        CASE
		            WHEN HOUR(su.visitedAt) BETWEEN 0 AND 2 THEN '0~3시'
		            WHEN HOUR(su.visitedAt) BETWEEN 3 AND 5 THEN '3~6시'
		            WHEN HOUR(su.visitedAt) BETWEEN 6 AND 8 THEN '6~9시'
		            WHEN HOUR(su.visitedAt) BETWEEN 9 AND 11 THEN '9~12시'
		            WHEN HOUR(su.visitedAt) BETWEEN 12 AND 14 THEN '12~15시'
		            WHEN HOUR(su.visitedAt) BETWEEN 15 AND 17 THEN '15~18시'
		            WHEN HOUR(su.visitedAt) BETWEEN 18 AND 20 THEN '18~21시'
		            ELSE '21~24시'
		        END,
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId,
		        CASE
		            WHEN HOUR(su.visitedAt) BETWEEN 0 AND 2 THEN '0~3시'
		            WHEN HOUR(su.visitedAt) BETWEEN 3 AND 5 THEN '3~6시'
		            WHEN HOUR(su.visitedAt) BETWEEN 6 AND 8 THEN '6~9시'
		            WHEN HOUR(su.visitedAt) BETWEEN 9 AND 11 THEN '9~12시'
		            WHEN HOUR(su.visitedAt) BETWEEN 12 AND 14 THEN '12~15시'
		            WHEN HOUR(su.visitedAt) BETWEEN 15 AND 17 THEN '15~18시'
		            WHEN HOUR(su.visitedAt) BETWEEN 18 AND 20 THEN '18~21시'
		            ELSE '21~24시'
		        END
		""")
		List<HourlyUsageDto> summarizeHourlyUsageByUser(@Param("startDate") LocalDateTime startDate);

	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.BrandUsageDto(
		        su.userId,
		        s.brand.name,
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    JOIN su.store s
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId, s.brand.name
		""")
		List<BrandUsageDto> summarizeBrandUsageByUser(@Param("startDate") LocalDateTime startDate);
	
	@Query("""
		    SELECT new com.ureca.ocean.jjh.batch.dto.StoreUsageDto(
		        su.userId,
		        su.store.name,
		        COUNT(su)
		    )
		    FROM StoreUsage su
		    WHERE su.visitedAt >= :startDate
		    GROUP BY su.userId, su.store.name
		""")
		List<StoreUsageDto> summarizeStoreUsageByUser(@Param("startDate") LocalDateTime startDate);

	@Query("""
		SELECT su FROM StoreUsage su
		JOIN FETCH su.store s
		JOIN FETCH s.brand
		WHERE su.userId = :userId
	""")
	List<StoreUsage> findAllByUserId(@Param("userId") UUID userId);

	boolean existsByUserIdAndStoreAndVisitedAt(UUID userId, Store store, LocalDateTime visitedAt);
}
