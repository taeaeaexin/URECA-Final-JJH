package com.ureca.ocean.jjh.direction.dto.response;

import com.ureca.ocean.jjh.direction.entity.DirectionHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectionResponseDto {
    private UUID id;
    private UUID userId;
    private double startLocationLatitude;
    private double startLocationLongitude;
    private double endLocationLatitude;
    private double endLocationLongitude;
    private boolean bookmark;
    private String kakaoRouteUrl;

    public static DirectionResponseDto from(DirectionHistory direction) {
        return DirectionResponseDto.builder()
                .id(direction.getId())
                .userId(direction.getUserId())
                .startLocationLatitude(direction.getStartLocationLatitude())
                .startLocationLongitude(direction.getStartLocationLongitude())
                .endLocationLatitude(direction.getEndLocationLatitude())
                .endLocationLongitude(direction.getEndLocationLongitude())
                .bookmark(direction.isBookmark())
                .kakaoRouteUrl(direction.getKakaoRouteUrl())
                .build();
    }
}
