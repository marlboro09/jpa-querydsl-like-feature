package com.sparta.dailyswitter.domain.follow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.sparta.dailyswitter.domain.follow.entity.Follow;
import com.sparta.dailyswitter.domain.user.entity.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, QuerydslPredicateExecutor<Follow> {
	List<Follow> findAllByFollowerUser(User followerUser);
	Follow findByFollowerUserAndFollowingUser(User followerUser, User followingUser);
}
