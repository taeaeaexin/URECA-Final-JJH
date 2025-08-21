package com.ureca.ocean.jjh.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@Schema(description = "게시글 작성 요청 DTO")
public class PostRequestDto {

    @Schema(description = "게시글 제목", example = "집앞에 gs25 가실 분!")
    private String title;

    @Schema(description = "게시글 내용", example = "ㅈㄱㄴ")
    private String content;

    @Schema(description = "카테고리", example = "편의점")
    private String category;

    @Schema(description = "브랜드 ID", example = "43a48689-606d-11f0-86b7-02ecb0678daf")
    private UUID brandId;

    @Schema(description = "혜택 ID", example = "35663531-3637-3131-2d36-3738632d3131")
    private UUID benefitId;

    @Schema(description = "약속 날짜", example = "2025-07-27T14:01:35.299Z")
    private LocalDateTime promiseDate;

    @Schema(description = "위치", example = "서울시 강남구 역삼동")
    private UUID StoreId;
}
