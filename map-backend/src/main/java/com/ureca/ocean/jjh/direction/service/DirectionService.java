package com.ureca.ocean.jjh.direction.service;

import com.ureca.ocean.jjh.direction.dto.request.DirectionRequestDto;
import com.ureca.ocean.jjh.direction.dto.request.PathFindRequestDto;
import com.ureca.ocean.jjh.direction.dto.response.DirectionResponseDto;
import com.ureca.ocean.jjh.direction.dto.response.PathFindResponseDto;
import com.ureca.ocean.jjh.direction.entity.DirectionHistory;
import com.ureca.ocean.jjh.mcp.dto.McpResponseDto;
import com.ureca.ocean.jjh.user.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface DirectionService {

    PathFindResponseDto getPath(String email, PathFindRequestDto requestDto);


    PathFindResponseDto getPathByLLM(String email, PathFindRequestDto requestDto);

    List<PathFindResponseDto> getMyDirectionHistory(String email);

    List<PathFindResponseDto> getAllBookmarkedDirections(String email);

    PathFindResponseDto getDirectionById(String id);


    PathFindResponseDto updateBookmark(String id, boolean bookMark);

    void deleteDirection(String id);
}
