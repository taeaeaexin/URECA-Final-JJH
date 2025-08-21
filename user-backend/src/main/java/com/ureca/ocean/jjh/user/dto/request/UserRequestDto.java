package com.ureca.ocean.jjh.user.dto.request;

import com.ureca.ocean.jjh.user.entity.enums.Gender;
import com.ureca.ocean.jjh.user.entity.enums.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String nickname;
    private String address;
    private String password;
    private String title;
    private Membership membership;

}
