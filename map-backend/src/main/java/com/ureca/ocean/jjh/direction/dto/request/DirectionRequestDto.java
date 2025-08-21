package com.ureca.ocean.jjh.direction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "길찾기 내역 저장 요청 DTO", example = "{\n" +
        "  \"startLocationLatitude\": 37.5665,\n" +
        "  \"startLocationLongitude\": 126.9780,\n" +
        "  \"endLocationLatitude\": 37.5796,\n" +
        "  \"endLocationLongitude\": 126.9770,\n" +
        "  \"bookmark\": true,\n" +
        "  \"kakaoRouteUrl\": \"https://map.kakao.com/?map_type=TYPE_MAP&target=walk&rt=523953%2C1084098%2C524419%2C1081931%2C524450%2C1082223&rt1=%EC%97%90%EC%9D%B4%EC%B9%98%EC%8A%A4%ED%80%98%EC%96%B4&rt2=%EC%95%8C%ED%8C%8C%EB%8F%94%ED%83%80%EC%9B%8C&rt3=%EC%B9%B4%EC%B9%B4%EC%98%A4%ED%8C%90%EA%B5%90%EC%95%84%EC%A7%80%ED%8A%B8&rtIds=%2C%2C&rtTypes=%2C%2C\"\n" +
        "}")

public class DirectionRequestDto {

    private double startLocationLatitude;
    private double startLocationLongitude;
    private double endLocationLatitude;
    private double endLocationLongitude;
    private boolean bookmark;
    private String kakaoRouteUrl;
}
