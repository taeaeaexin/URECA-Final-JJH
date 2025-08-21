package com.ureca.ocean.jjh.brand.repository;

import com.ureca.ocean.jjh.brand.entity.Brand;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    // Brand 이름 검색, 정렬
    List<Brand> findByNameContaining(String keyword, Sort sortBy);

    Optional<Brand> findById(UUID id);

    @Query("SELECT COUNT(b) FROM Brand b")
    int countAllBrand();
}