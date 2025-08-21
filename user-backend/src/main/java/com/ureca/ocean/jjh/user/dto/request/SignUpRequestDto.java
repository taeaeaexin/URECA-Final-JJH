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
public class SignUpRequestDto {
    private String name;         // nullable = false
    private String nickname;     // nullable = false
    private String email;        // nullable = false
    private String password;     // nullable = false
    private Gender gender;       // nullable = false
}
