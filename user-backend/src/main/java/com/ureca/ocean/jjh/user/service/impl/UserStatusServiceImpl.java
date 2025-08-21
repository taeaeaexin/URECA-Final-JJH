package com.ureca.ocean.jjh.user.service.impl;

import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.user.dto.response.UserStatusResponseDto;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.entity.UserStatus;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import com.ureca.ocean.jjh.user.repository.UserStatusRepository;
import com.ureca.ocean.jjh.user.service.UserStatusService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class UserStatusServiceImpl implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto getUserStatus(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserException(ErrorCode.NOT_FOUND_USER));

        UserStatus userStatus = userStatusRepository.findById(user.getId()).orElseThrow(()->new UserException(ErrorCode.USER_STATUS_NOT_EXIST));

        return UserStatusResponseDto.builder()
                .userId(userStatus.getId())
                .exp(userStatus.getExp())
                .level(userStatus.getLevel())
                .build();

    }

    @Override
    public UserStatusResponseDto changeUserStatus(String email, Long expChange) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        UserStatus userStatusFound = userStatusRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(ErrorCode.USER_STATUS_NOT_EXIST));

        Long beforeLevel = userStatusFound.getLevel();
        Long beforeExp = userStatusFound.getExp();

        // 총 경험치 계산
        Long totalExp = beforeLevel * 50 + beforeExp + expChange;

        // 레벨과 잔여 경험치 재계산
        Long newLevel = totalExp / 50;
        Long newExp = totalExp % 50;

        // 음수 경험치 보정
        if (newExp < 0) {
            newLevel -= 1;
            newExp += 50;
        }

        // 최저 레벨은 0
        if (newLevel < 0) {
            newLevel = 0L;
            newExp = 0L;
        }

        boolean levelChanged = !newLevel.equals(beforeLevel);

        userStatusFound.setLevel(newLevel);
        userStatusFound.setExp(newExp);
        UserStatus userStatus = userStatusRepository.save(userStatusFound);

        return UserStatusResponseDto.builder()
                .userId(userStatus.getId())
                .exp(userStatus.getExp())
                .level(userStatus.getLevel())
                .isLevelUpdated(levelChanged)
                .build();
    }



}
