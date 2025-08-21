package com.ureca.ocean.jjh.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserAndStatusResponseDto {
    private UUID id;
    private String name;
    private String nickname;
    private String title;
    private int level;
}