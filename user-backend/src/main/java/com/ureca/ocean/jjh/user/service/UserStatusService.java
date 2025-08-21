package com.ureca.ocean.jjh.user.service;

import com.ureca.ocean.jjh.user.dto.response.UserStatusResponseDto;

public interface UserStatusService {
    UserStatusResponseDto getUserStatus(String email);

    UserStatusResponseDto changeUserStatus(String email, Long expChange);
}
