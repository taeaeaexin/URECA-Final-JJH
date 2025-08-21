package com.ureca.ocean.jjh.bookmarks.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequestDto {
	private UUID userId;
	private UUID storeId;
}
