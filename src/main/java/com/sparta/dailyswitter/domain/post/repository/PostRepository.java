package com.sparta.dailyswitter.domain.post.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.user.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findByTitleContainingAndCreatedAtBetween(
		String title,
		LocalDateTime start,
		LocalDateTime end,
		Pageable pageable
	);

	Page<Post> findByTitleContainingAndCreatedAtBetweenAndUserIn(
		String title,
		LocalDateTime start,
		LocalDateTime end,
		List<User> users,
		Pageable pageable
	);
}