package com.ureca.ocean.jjh.benefit.entity;

import com.ureca.ocean.jjh.brand.entity.Brand;
import com.ureca.ocean.jjh.common.enums.Membership;
import com.ureca.ocean.jjh.storeUsage.entity.StoreUsage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benefits")
@Getter
@Setter
public class Benefit {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership")
    private Membership membership;

    @Column(name = "benefit_value")
    private int benefitValue;

    @Column(name = "benefit_unit")
    private String benefitUnit;

    @Column(name = "description")
    private String description;

    // 연관관계
    @OneToMany(mappedBy = "benefit")
    private List<StoreUsage> storeUsages = new ArrayList<>();
}
