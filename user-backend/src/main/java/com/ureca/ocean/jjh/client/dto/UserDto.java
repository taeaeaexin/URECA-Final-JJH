package com.ureca.ocean.jjh.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
//@Builder 없다. 
public class UserDto {

	private UUID id;
	private String name;
	private String email;
	private String password;
}
