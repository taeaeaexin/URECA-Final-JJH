package com.ureca.ocean.jjh.user.repository;

import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    //crud 자동 생성
}
