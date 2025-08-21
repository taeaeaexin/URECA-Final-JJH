package com.ureca.ocean.jjh.direction.entity;

import com.ureca.ocean.jjh.common.entity.BaseEntity;
import com.ureca.ocean.jjh.direction.dto.request.DirectionRequestDto;
import com.ureca.ocean.jjh.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;
@Entity
@Table(name = "find_directions_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectionHistory extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    // User 엔티티 대신 ID만 저장
    @Column(name = "id2", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "start_location_latitude", nullable = false)
    private double startLocationLatitude;

    @Column(name = "start_location_longitude", nullable = false)
    private double startLocationLongitude;

    @Column(name = "end_location_latitude", nullable = false)
    private double endLocationLatitude;

    @Column(name = "end_location_longitude", nullable = false)
    private double endLocationLongitude;

    @Column(name = "bookmark", nullable = false)
    private boolean bookmark;

    @Column(name = "kakao_route_url", length = 1000)
    private String kakaoRouteUrl;

    public static DirectionHistory from(UserDto userDto, DirectionRequestDto dto) {
        return DirectionHistory.builder()
                .userId(userDto.getId())
                .startLocationLatitude(dto.getStartLocationLatitude())
                .startLocationLongitude(dto.getStartLocationLongitude())
                .endLocationLatitude(dto.getEndLocationLatitude())
                .endLocationLongitude(dto.getEndLocationLongitude())
                .bookmark(dto.isBookmark())
                .kakaoRouteUrl(dto.getKakaoRouteUrl())
                .build();
    }
}
