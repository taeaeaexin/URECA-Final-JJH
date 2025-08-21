package com.ureca.ocean.jjh.user.service.impl;

import com.ureca.ocean.jjh.exception.ErrorCode;

import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.user.dto.request.SignUpRequestDto;
import com.ureca.ocean.jjh.user.dto.request.UserRequestDto;
import com.ureca.ocean.jjh.user.dto.response.UserAndStatusResponseDto;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDto;
import com.ureca.ocean.jjh.user.dto.response.UserResponseDtoWithPassword;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.entity.UserStatus;
import com.ureca.ocean.jjh.user.entity.enums.Membership;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import com.ureca.ocean.jjh.user.repository.UserStatusRepository;
import com.ureca.ocean.jjh.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    @Override
    public UserResponseDtoWithPassword getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.NOT_FOUND_USER));

        return UserResponseDtoWithPassword.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .address(user.getAddress())
                .title(user.getTitle())
                .gender(user.getGender())
                .nickname(user.getNickname())
                .membership(user.getMembership())
                .build();
    }


    @Override
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        if( userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()){
            throw new UserException(ErrorCode.USER_ALREADY_EXIST);
        }
        //사용자 테이블 insert
        User user = new User();
        user.setName(signUpRequestDto.getName());
        user.setEmail(signUpRequestDto.getEmail());
        user.setAddress("initial-address");
        user.setGender(signUpRequestDto.getGender());
        user.setMembership(Membership.우수);
        user.setNickname(signUpRequestDto.getNickname());

        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        //사용자 status 테이블 insert
        UserStatus userStatus = UserStatus.builder()
                .user(savedUser)
                .level(0L)
                .exp(0L)
                .build();
        try{
            userStatusRepository.save(userStatus);
        }catch(Exception e){
            throw new UserException(ErrorCode.USER_STATUS_SAVE_FAIL);
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .address(user.getAddress())
                .title(user.getTitle())
                .gender(user.getGender())
                .nickname(user.getNickname())
                .membership(user.getMembership())
                .build();
    }
    @Override
    public boolean getIsDupNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public boolean getIsDupEmail(String email) { return userRepository.existsByEmail(email); }
    @Override
    public UserResponseDto getCurrentUserInfo(String email){
        UserResponseDto userDto = new UserResponseDto();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            userDto = UserResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .address(user.getAddress())
                    .title(user.getTitle())
                    .gender(user.getGender())
                    .nickname(user.getNickname())
                    .membership(user.getMembership())
                    .build();
        }

        return userDto;
    }

    @Override
    @Transactional
    public UserResponseDto updateUserInfo(String email, UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        // 닉네임 변경 시 중복 체크
        if (userRequestDto.getNickname() != null && !userRequestDto.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(userRequestDto.getNickname())) {
                throw new UserException(ErrorCode.USER_ALREADY_EXIST);
            }
            user.setNickname(userRequestDto.getNickname());
        }

        // 주소 변경
        if (userRequestDto.getAddress() != null) {
            user.setAddress(userRequestDto.getAddress());
        }

        // 비밀번호 변경 (암호화 후 저장)
        if (userRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        // title 변경
        if (userRequestDto.getTitle() != null) {
            user.setTitle(userRequestDto.getTitle());
        }

        // membership 변경
        if (userRequestDto.getMembership() != null) {

            user.setMembership(userRequestDto.getMembership());
        }

        User updatedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .nickname(updatedUser.getNickname())    // 닉네임도 리턴하도록 추가 추천
                .address(updatedUser.getAddress())      // 필요하면 추가
                .title(updatedUser.getTitle())
                .membership(updatedUser.getMembership())
                .gender(updatedUser.getGender())
                .build();
    }

    @Override
    public List<UserAndStatusResponseDto> getAllUserAndStatus() {
        // users
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new UserException(ErrorCode.NOT_FOUND_USER);

        // user status
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        if (userStatuses.isEmpty()) throw new UserException(ErrorCode.USER_STATUS_SAVE_FAIL);

        return users.stream().map(user -> {
            UserStatus status = userStatuses.stream()
                    .filter(us -> us.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(UserStatus.builder().level(0L).build());
            return UserAndStatusResponseDto.of(user, status.getLevel().intValue());
        }).toList();
    }
}
