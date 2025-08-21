package com.ureca.ocean.jjh.direction.controller;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.direction.dto.request.DirectionRequestDto;
import com.ureca.ocean.jjh.direction.dto.request.PathFindRequestDto;
import com.ureca.ocean.jjh.direction.service.impl.DirectionServiceImpl;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/map/direction")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Direction API", description = "길찾기 관련 API")
public class DirectionController {

    private final DirectionServiceImpl directionService;
    private final UserClient userClient;

    @PostMapping("/path")
    @Operation(summary = "자동차 길 찾기", description = "자동차 길찾기를 제공합니다.")
    public ResponseEntity<BaseResponseDto<?>> getPath(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-email", required = false) String encodedEmail,
            @RequestBody PathFindRequestDto pathFindRequestDto) {

        String email = (encodedEmail != null) ? URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8) : null;
        return ResponseEntity.ok(BaseResponseDto.success(directionService.getPath(email, pathFindRequestDto)));
    }

    @PostMapping("/pathByLLM")
    @io.swagger.v3.oas.annotations.Operation(summary = "ai 기반 경유지 포함 자동차 길 찾기", description = "ai가 사용자의 이용패턴, 현재 시간등을 분석하여 경유해서 가면 좋을 제휴처들을 지나가는 경로를 만들어줍니다.")
    public ResponseEntity<BaseResponseDto<?>> getPathByLLM(
            @io.swagger.v3.oas.annotations.Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @RequestBody PathFindRequestDto pathFindRequestDto){
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        return ResponseEntity.ok(BaseResponseDto.success(directionService.getPathByLLM(email,pathFindRequestDto)));
    }

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "나의 길찾기 내역 조회", description = "나의 길찾기 내역 조회")
    public ResponseEntity<BaseResponseDto<?>> getMyDirectionsHistory(@io.swagger.v3.oas.annotations.Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        return ResponseEntity.ok(BaseResponseDto.success(directionService.getMyDirectionHistory(email)));
    }

    @GetMapping("/bookmark")
    @io.swagger.v3.oas.annotations.Operation(summary = "전체 길찾기 즐겨찾기 조회", description = "즐겨찾기한 길찾기 기록들을 조회합니다.")
    public ResponseEntity<BaseResponseDto<?>> getAllBookmarkedDirections(@io.swagger.v3.oas.annotations.Parameter(hidden = true) @RequestHeader("X-User-email") String encodedEmail) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        return ResponseEntity.ok(BaseResponseDto.success(directionService.getAllBookmarkedDirections(email)));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "특정 길찾기 기록 조회", description = "ID로 Direction 기록을 조회합니다.")
    public ResponseEntity<BaseResponseDto<?>> getDirectionById(
            @io.swagger.v3.oas.annotations.Parameter(description = "조회할 Direction ID", required = true)
            @PathVariable String id) {
        return ResponseEntity.ok(BaseResponseDto.success(directionService.getDirectionById(id)));
    }

    @PutMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "길찾기 기록 수정", description = "ID에 해당하는 길찾기 기록의 즐겨찾기를 수정합니다.")
    public ResponseEntity<BaseResponseDto<?>> updateBookmark(
            @io.swagger.v3.oas.annotations.Parameter(description = "수정할 Direction ID", required = true)
            @RequestParam String id,
            @io.swagger.v3.oas.annotations.Parameter(description = "수정될 bookmark 값", required = true)
            @RequestParam Boolean bookmark) {
        return ResponseEntity.ok(BaseResponseDto.success(directionService.updateBookmark(id, bookmark)));
    }

    @DeleteMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "길찾기 기록 삭제", description = "ID에 해당하는 Direction 기록을 삭제합니다.")
    public ResponseEntity<BaseResponseDto<?>> deleteDirection(
            @io.swagger.v3.oas.annotations.Parameter(description = "삭제할 Direction ID", required = true)
            @PathVariable String id) {
        directionService.deleteDirection(id);
        return ResponseEntity.ok(BaseResponseDto.success("삭제 완료"));
    }
}
