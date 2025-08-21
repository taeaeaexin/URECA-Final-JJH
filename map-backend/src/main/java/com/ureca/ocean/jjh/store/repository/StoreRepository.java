package com.ureca.ocean.jjh.store.repository;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.brand.entity.Brand;
import com.ureca.ocean.jjh.store.dto.StoreRankDto;
import com.ureca.ocean.jjh.store.entity.Store;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {
    @Query("SELECT s FROM Store s JOIN FETCH s.brand b JOIN FETCH b.benefits WHERE s.id = :id")
    Optional<Store> findById(@Param("id") UUID id);

    Benefit findBenefitById(UUID id);

    @Query("SELECT DISTINCT s.category FROM Store s")
    List<String> findDistinctCategories();


    @Query("SELECT DISTINCT s.brand FROM Store s WHERE s.category = :category")
    List<Brand> findDistinctBrandByCategory(String category);

//    @Query("SELECT DISTINCT s FROM Store s " +
//           "JOIN FETCH s.brand b " +
//           "JOIN FETCH s.storeUsages su " +
//           "GROUP BY s " +
//           "HAVING COUNT(su.id) > 0 " +
//           "ORDER BY COUNT(su.id) DESC")
//    List<Store> findStoreByStoreUsageDesc();
    @Query("SELECT new com.ureca.ocean.jjh.store.dto.StoreRankDto(" +
            "s.id, s.name, COUNT(su), s.address, s.category, s.latitude, s.longitude, " +
            "b.name, b.imageUrl) " +
            "FROM StoreUsage su " +
            "JOIN su.store s " +
            "JOIN s.brand b " +
            "GROUP BY s.id, s.name, s.address, s.category, s.latitude, s.longitude, b.name, b.imageUrl " +
            "ORDER BY COUNT(su) DESC")

    List<StoreRankDto> findStoreByStoreUsageDesc();
}