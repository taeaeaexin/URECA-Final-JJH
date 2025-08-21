package com.ureca.ocean.jjh.direction.repository;

import com.ureca.ocean.jjh.direction.entity.DirectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DirectionHistoryRepository extends JpaRepository<DirectionHistory, UUID> {
    List<DirectionHistory> findByBookmark(boolean bookmark);
}
