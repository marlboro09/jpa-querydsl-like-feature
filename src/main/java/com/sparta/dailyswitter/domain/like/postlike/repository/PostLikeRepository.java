package com.sparta.dailyswitter.domain.like.postlike.repository;

import com.sparta.dailyswitter.domain.like.postlike.entity.PostLike;
import com.sparta.dailyswitter.domain.like.postlike.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId>,
    QuerydslPredicateExecutor<PostLike> {

}
