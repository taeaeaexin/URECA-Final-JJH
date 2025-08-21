package com.ureca.ocean.jjh.bookmarks.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ureca.ocean.jjh.bookmarks.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID>{

	List<Bookmark> findByUserId(UUID userId);
	Optional<Bookmark> findByUserIdAndStoreId(UUID userId, UUID storeId);
	boolean existsByUserIdAndStoreId(UUID userId, UUID storeId);
}
