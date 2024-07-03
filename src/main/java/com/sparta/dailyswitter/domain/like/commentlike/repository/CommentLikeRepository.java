package com.sparta.dailyswitter.domain.like.commentlike.repository;

import com.sparta.dailyswitter.domain.like.commentlike.entity.CommentLike;
import com.sparta.dailyswitter.domain.like.commentlike.entity.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId>,
    QuerydslPredicateExecutor<CommentLike> {

}
