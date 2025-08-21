package com.ureca.ocean.jjh.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserRankDto {
    private UUID id;
    private String name;
    private String nickname;
    private String title;
    private double completePercentage;
    private int level;
    private int storeUsage;

    // UserRank → UserRankDto 변환용 정적 메서드
    public static UserRankDto from(UserAndStatusResponseDto user, double completePercentage, int storeUsage) {
        return UserRankDto.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .title(user.getTitle())
                .completePercentage(completePercentage)
                .level(user.getLevel())
                .storeUsage(storeUsage)
                .build();
    }
}