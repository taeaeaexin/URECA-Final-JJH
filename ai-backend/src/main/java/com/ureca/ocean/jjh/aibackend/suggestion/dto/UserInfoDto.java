package com.ureca.ocean.jjh.aibackend.suggestion.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

	private String gender;
	private String nickname;
	private List<String> recentVisitedStores;
	private List<String> recentBenefits;
	private List<String> favorites;
}
