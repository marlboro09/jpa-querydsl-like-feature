package com.sparta.dailyswitter.domain.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.user.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
	Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
	Page<Post> findByUserInOrderByCreatedAtDesc(List<User> users, Pageable pageable);
	Page<Post> findAllByOrderByIsPinnedDescCreatedAtDesc(Pageable pageable);
}