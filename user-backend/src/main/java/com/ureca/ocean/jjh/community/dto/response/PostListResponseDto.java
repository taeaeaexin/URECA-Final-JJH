package com.ureca.ocean.jjh.community.dto.response;

import com.ureca.ocean.jjh.community.entity.Post;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostListResponseDto {
    private List<PostResponseDto> postList;

    public static PostListResponseDto of(List<PostResponseDto> postList) {
        return PostListResponseDto.builder()
                .postList(postList)
                .build();
    }
}
