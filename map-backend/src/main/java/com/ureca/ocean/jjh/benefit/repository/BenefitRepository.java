package com.ureca.ocean.jjh.benefit.repository;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.common.enums.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BenefitRepository extends JpaRepository<Benefit, UUID> {
    List<Benefit> getBenefitByBrandId(UUID brandId);

    Optional<Benefit> findById(UUID benefitId);

    Optional<Benefit> findBenefitByBrandIdAndMembership(UUID brandId, Membership membership);
}