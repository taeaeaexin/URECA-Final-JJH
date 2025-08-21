package com.ureca.ocean.jjh.storeUsage.entity;

import com.ureca.ocean.jjh.benefit.entity.Benefit;
import com.ureca.ocean.jjh.gemini.dto.OcrGeminiResponseDto;
import com.ureca.ocean.jjh.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_usages")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
public class StoreUsage {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Column(name = "benefit_amount", nullable = false)
    private int benefitAmount;

    public static StoreUsage from(OcrGeminiResponseDto dto, UUID userId, Store store, Benefit benefit, int benefitAmount) {
        return StoreUsage.builder()
                .userId(userId)
                .store(store)
                .benefit(benefit)
                .visitedAt(dto.getVisitedAt())
                .benefitAmount(benefitAmount)
                .build();
    }
}
