package com.ureca.ocean.jjh.user.dto;

import com.ureca.ocean.jjh.common.enums.Gender;
import com.ureca.ocean.jjh.common.enums.Membership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String password;
    
//    private String nickname;
//    private Gender gender;
//    private String address;
//    private Date createdAt;
//    private Date updatedAt;
//    private Membership membership;
//    private String title;
}
