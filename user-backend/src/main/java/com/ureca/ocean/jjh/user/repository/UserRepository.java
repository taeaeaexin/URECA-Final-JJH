package com.ureca.ocean.jjh.user.repository;

import com.ureca.ocean.jjh.user.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByNickname(String nickname);
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email); 
}
