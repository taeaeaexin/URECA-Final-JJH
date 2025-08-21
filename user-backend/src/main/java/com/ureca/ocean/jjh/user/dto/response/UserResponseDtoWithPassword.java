package com.ureca.ocean.jjh.user.dto.response;

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
public class UserResponseDtoWithPassword {

	private UUID id;
	private String name;
	private String email;
	private String password;
	private String address;
	private Gender gender;
	private String title;
	private Membership membership;
	private String nickname;

}
