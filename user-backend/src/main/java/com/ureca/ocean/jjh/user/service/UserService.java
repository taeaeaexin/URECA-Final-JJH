package com.ureca.ocean.jjh.user.service;

import com.ureca.ocean.jjh.user.dto.request.SignUpRequestDto;
import com.ureca.ocean.jjh.user.dto.request.UserRequestDto;
import com.ureca.ocean.jjh.user.dto.response.UserAndStatusResponseDto;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDto;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDtoWithPassword;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) ;
    public UserResponseDtoWithPassword getUserByEmail(String email);

    boolean getIsDupNickname(String nickname);


    boolean getIsDupEmail(String email);

    UserResponseDto getCurrentUserInfo(String email);


    @Transactional
    UserResponseDto updateUserInfo(String email, UserRequestDto userRequestDto);

    List<UserAndStatusResponseDto> getAllUserAndStatus();
}
