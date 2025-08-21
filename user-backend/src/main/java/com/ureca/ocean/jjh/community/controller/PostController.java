package com.ureca.ocean.jjh.community.controller;

import com.ureca.ocean.jjh.common.BaseResponseDto;
import com.ureca.ocean.jjh.community.dto.response.PostListResponseDto;
import com.ureca.ocean.jjh.community.dto.response.PostResponseDto;
import com.ureca.ocean.jjh.community.dto.request.PostRequestDto;
import com.ureca.ocean.jjh.community.service.impl.PostServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/article")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostServiceImpl postServiceImpl;

    @Operation(summary = "게시글 작성", description = "사용자가 게시글을 작성합니다. 글의 주소는 사용자 정보에서 가져와서 등록합니다. 사용자 정보 중 주소 영역에 '시-군-구-동' 이렇게 넣었다고 가정할 때, '동' (즉, 마지막 단어)을 가져와서 글의 위치로 등록하고 있습니다. ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공"),
    })
    @PostMapping
    public ResponseEntity<BaseResponseDto<?>> insertPost(
            @Parameter(description = "Base64 인코딩된 사용자 이메일", hidden = true) @RequestHeader("X-User-email") String encodedEmail,
            @RequestBody PostRequestDto postRequestDto) {

        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        PostResponseDto postResponseDto = postServiceImpl.insertPost(email, postRequestDto);
        return ResponseEntity.ok(BaseResponseDto.success(postResponseDto));
    }

    @Operation(summary = "게시글 위치 목록 조회", description = "게시글에 등록된 지역(위치) 목록들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위치 목록 조회 성공"),
    })
    @GetMapping("/locations")
    public ResponseEntity<BaseResponseDto<?>> listLocations() {
        return ResponseEntity.ok(BaseResponseDto.success(postServiceImpl.listLocations()));
    }

    @Operation(summary = "게시글 목록 조회", description = "전체 게시글 목록을 페이지네이션과 정렬 기준, 위치 필터로 조회합니다.(아무것도 없으면 전체 조회입니다.  필요한것만 넣으시면 됩니다.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<?>> listPost(
            @Parameter(description = "페이지 번호 (0부터 시작), 페이지 하나당 10개씩", example = "0")
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,

            @Parameter(description = "정렬 기준 (현재 createdAt만 지원)", example = "createdAt")
            @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria,

            @Parameter(description = "사용자의 현위치를 '시-군-구-동' 이렇게 넣었다고 가정할 때, '동'을 가져와서 글의 위치로 등록하고 있습니다.", example = "역삼동")
            @RequestParam(required = false, defaultValue = "", value = "location") String location) {

        PostListResponseDto postListResponseDto = PostListResponseDto.of(postServiceImpl.listPost(pageNo, criteria, location));
        return ResponseEntity.ok(BaseResponseDto.success(postListResponseDto));
    }
    @Operation(summary = "게시글 상세 조회", description = "게시글을 id 값을 통해 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
    })
    @GetMapping("/detail")
    public ResponseEntity<BaseResponseDto<?>> detailPost(@Parameter(description = "글 id ", example = "ccc0e0ca-ce19-4d99-9f98-e283e7f4102e") @RequestParam UUID postId) {
        PostResponseDto postResponseDto = postServiceImpl.detailPost(postId);
        return ResponseEntity.ok(BaseResponseDto.success(postResponseDto));
    }

    @Operation(summary = "본인이 쓴 게시글 목록 조회", description = "본인이 쓴 게시글 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
    })
    @GetMapping("/myPost")
    public ResponseEntity<BaseResponseDto<?>> getMyPost(
            @Parameter(description = "Base64 인코딩된 사용자 이메일", hidden = true)
            @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "페이지 번호 (0부터 시작), 페이지 하나당 10개씩", example = "0")
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
            @Parameter(description = "정렬 기준 (현재 createdAt만 지원)", example = "createdAt")
            @RequestParam(required = false, defaultValue = "createdAt", value = "criteria") String criteria
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        List<PostResponseDto> postResponseDto = postServiceImpl.getMyPost(pageNo,criteria,email);
        return ResponseEntity.ok(BaseResponseDto.success(postResponseDto));
    }

    @Operation(summary = "게시글 삭제", description = "게시글 ID를 통해 본인이 작성한 게시글을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<BaseResponseDto<?>> deletePost(
            @Parameter(description = "Base64 인코딩된 사용자 이메일", hidden = true)
            @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "삭제할 게시글 ID", example = "7391e17f-9f20-420e-ab75-e49af111b75a")
            @RequestParam UUID postId) {

        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        postServiceImpl.deletePost(email, postId);
        return ResponseEntity.ok(BaseResponseDto.success("게시글 삭제 성공"));
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID를 통해 본인이 작성한 게시글을 수정합니다.")
    @PutMapping
    public ResponseEntity<BaseResponseDto<?>> updatePost(
            @Parameter(description = "Base64 인코딩된 사용자 이메일", hidden = true)
            @RequestHeader("X-User-email") String encodedEmail,

            @Parameter(description = "수정할 게시글 ID", example = "095ce195-971c-485b-8adc-af1c1c186fb4")
            @RequestParam UUID postId,

            @RequestBody PostRequestDto postUpdateRequestDto) {

        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        PostResponseDto updatedPost = postServiceImpl.updatePost(email, postId, postUpdateRequestDto);
        return ResponseEntity.ok(BaseResponseDto.success(updatedPost));
    }




}
