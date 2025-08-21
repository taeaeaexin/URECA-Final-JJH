package com.ureca.ocean.jjh.aibackend.client.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private UUID id;
	private String name;
	private String email;
	private String password;
}
