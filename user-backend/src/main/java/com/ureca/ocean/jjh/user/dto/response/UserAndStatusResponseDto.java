package com.ureca.ocean.jjh.user.dto.response;

import com.ureca.ocean.jjh.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAndStatusResponseDto {
    private UUID id;
    private String name;
    private String nickname;
    private String title;
    private int level;

    public static UserAndStatusResponseDto of(User user, int level) {
        return UserAndStatusResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .title(user.getTitle())
                .level(level)
                .build();
    }
}