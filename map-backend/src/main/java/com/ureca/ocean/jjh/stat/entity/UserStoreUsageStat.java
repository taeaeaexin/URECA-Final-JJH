package com.ureca.ocean.jjh.stat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ureca.ocean.jjh.stat.entity.enums.Period;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "user_store_usage_stat")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStoreUsageStat {

	@Id
    private String id;
    private UUID userId;
    private Period period;
    private int count;
    private String storeName;
    private LocalDateTime updatedAt;

}
