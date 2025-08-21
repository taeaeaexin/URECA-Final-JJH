package com.ureca.ocean.jjh.direction.repository;

import com.ureca.ocean.jjh.direction.dto.response.PathFindResponseDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface PathFindRepository extends MongoRepository<PathFindResponseDto, String> {
    // 기본 CRUD 메서드 제공됨

    List<PathFindResponseDto> findByUserId(UUID userId);
    List<PathFindResponseDto> findByBookmarkAndUserId(boolean bookmark, UUID userId);
}
