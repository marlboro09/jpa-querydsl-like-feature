package com.sparta.dailyswitter.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.dailyswitter.domain.comment.entity.Comment;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
	List<Comment> findByPostId(Long postId);
	List<Comment> findAllByOrderByCreatedAtDesc();
}
