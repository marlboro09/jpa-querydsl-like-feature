package com.sparta.dailyswitter.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.sparta.dailyswitter.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
	Optional<User> findByUserId(String userId);

	Optional<User> findByEmail(String email);

	Optional<User> findByKakaoId(String kakaoId);

	Optional<User> findByNaverId(String naverId);
}

