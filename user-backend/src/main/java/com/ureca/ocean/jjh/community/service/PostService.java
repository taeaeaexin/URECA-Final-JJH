package com.ureca.ocean.jjh.community.service;

import com.ureca.ocean.jjh.community.dto.response.PostResponseDto;
import com.ureca.ocean.jjh.community.dto.request.PostRequestDto;

import java.util.List;
import java.util.UUID;

public interface PostService {
    PostResponseDto insertPost(String email, PostRequestDto postRequestDto);

    List<PostResponseDto> getMyPost(int pageNo, String criteria, String email);

    List<PostResponseDto> listPost(int pageNo, String criteria, String location);

    PostResponseDto detailPost(UUID postId);

    List<String> listLocations();

    void deletePost(String email, UUID postId);

    PostResponseDto updatePost(String email, UUID postId, PostRequestDto dto);
}
