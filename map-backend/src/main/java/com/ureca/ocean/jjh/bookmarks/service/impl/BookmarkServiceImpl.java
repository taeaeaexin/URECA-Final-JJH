package com.ureca.ocean.jjh.bookmarks.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ureca.ocean.jjh.bookmarks.dto.request.BookmarkRequestDto;
import com.ureca.ocean.jjh.bookmarks.entity.Bookmark;
import com.ureca.ocean.jjh.bookmarks.repository.BookmarkRepository;
import com.ureca.ocean.jjh.bookmarks.service.BookmarkService;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.MapException;
import com.ureca.ocean.jjh.store.dto.StoreDto;
import com.ureca.ocean.jjh.store.entity.Store;
import com.ureca.ocean.jjh.store.repository.StoreRepository;
import com.ureca.ocean.jjh.user.client.UserClient;
import com.ureca.ocean.jjh.user.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService{
	
	private final BookmarkRepository bookmarkRepository;
	private final StoreRepository storeRepository;
	private final UserClient userClient;
	
	@Override
	@Transactional(readOnly = true)
	public List<StoreDto> getBookmarksByUserId(String email, List<String> categories) {
		
		UserDto user = userClient.getUserByEmail(email);

		if (user == null || user.getId() == null) {
			throw new MapException(ErrorCode.USER_ID_PARSING_ERROR);
		}

		List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());

		List<StoreDto> bookmarkList = bookmarks.stream()
			.map(bookmark -> {
				Store store = bookmark.getStore();
				return StoreDto.from(store);
			})
			.filter(storeDto -> categories == null || categories.isEmpty() ||
				categories.stream().anyMatch(c -> storeDto.getCategory() != null && c.equalsIgnoreCase(storeDto.getCategory())))
			.collect(Collectors.toList());
		
		return bookmarkList;
	}

	@Override
	public void addBookmark(String email, UUID storeId) {
		
		UserDto user = userClient.getUserByEmail(email);
		
		if (bookmarkRepository.existsByUserIdAndStoreId(user.getId(), storeId)) {
			throw new MapException(ErrorCode.BOOKMARD_ALREADY_EXIST);
		}
		
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new MapException(ErrorCode.NOT_FOUND_ERROR));
		
		Bookmark bookmark = new Bookmark();
		bookmark.setUserId(user.getId());
		bookmark.setStore(store);
		
		bookmarkRepository.save(bookmark);
	}

	@Override
	public void deleteBookmark(String email, UUID storeId) {
		
		UserDto user = userClient.getUserByEmail(email);
		
		Bookmark bookmark = bookmarkRepository.findByUserIdAndStoreId(user.getId(), storeId)
				.orElseThrow(() -> new MapException(ErrorCode.NOT_FOUND_ERROR));
		
		bookmarkRepository.delete(bookmark);
	}
	
}
