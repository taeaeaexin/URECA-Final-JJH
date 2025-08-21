package com.ureca.ocean.jjh.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Builder
@Getter @Setter
public class UserStatusResponseDto {
    private UUID userId;
    private Long level;
    private Long exp;
    private boolean isLevelUpdated;
}
