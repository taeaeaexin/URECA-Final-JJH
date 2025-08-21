package com.ureca.ocean.jjh.bookmarks.service;

import java.util.List;
import java.util.UUID;

import com.ureca.ocean.jjh.bookmarks.dto.request.BookmarkRequestDto;
import com.ureca.ocean.jjh.store.dto.StoreDto;


public interface BookmarkService {
	
    List<StoreDto> getBookmarksByUserId(String email, List<String> categories);
    void addBookmark(String email, UUID storeId);
    void deleteBookmark(String email, UUID storeId);
    
}
