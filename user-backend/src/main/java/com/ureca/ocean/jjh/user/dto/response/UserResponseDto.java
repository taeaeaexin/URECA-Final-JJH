package com.ureca.ocean.jjh.user.dto.response;

import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.entity.enums.Gender;
import com.ureca.ocean.jjh.user.entity.enums.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

	private UUID id;
	private String name;
	private String email;
	private String address;
	private Gender gender;
	private String title;
	private Membership membership;
	private String nickname;

	public static UserResponseDto of(User user) {
		return UserResponseDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.address(user.getAddress())
				.gender(user.getGender())
				.title(user.getTitle())
				.membership(user.getMembership())
				.nickname(user.getNickname())
				.build();
	}

}
