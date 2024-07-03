package com.sparta.dailyswitter.domain.like.postlike.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.like.postlike.entity.PostLike;
import com.sparta.dailyswitter.domain.like.postlike.entity.PostLikeId;
import com.sparta.dailyswitter.domain.like.postlike.entity.QPostLike;
import com.sparta.dailyswitter.domain.like.postlike.repository.PostLikeRepository;
import com.sparta.dailyswitter.domain.post.dto.PostResponseDto;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.post.service.PostService;
import com.sparta.dailyswitter.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public void createPostLike(Long postId, User user) {
        Post post = postService.findById(postId);

        PostLikeId postLikeId = PostLikeId.builder()
            .user(user)
            .post(post)
            .build();

        if (postLikeRepository.findById(postLikeId).isPresent()) {
            throw new CustomException(ErrorCode.POST_LIKE_EXIST);
        }

        postService.checkPostUserFound(post, user);

        PostLike postLike = PostLike.builder()
            .id(postLikeId)
            .build();

        postLikeRepository.save(postLike);
        post.addPostLikes();
    }

    @Transactional
    public void deletePostLike(Long postId, User user) {
        Post post = postService.findById(postId);

        PostLikeId postLikeId = PostLikeId.builder()
            .user(user)
            .post(post)
            .build();

        if (postLikeRepository.findById(postLikeId).isEmpty()) {
            throw new CustomException(ErrorCode.POST_LIKE_NOT_EXIST);
        }

        postService.checkPostUserFound(post, user);

        PostLike postLike = PostLike.builder()
            .id(postLikeId)
            .build();

        postLikeRepository.delete(postLike);
        post.subPostLikes();
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getLikedPosts(User user, Pageable pageable) {
        QPostLike qPostLike = QPostLike.postLike;

        List<Post> posts = jpaQueryFactory.select(qPostLike.id.post)
            .from(qPostLike)
            .where(qPostLike.id.user.eq(user))
            .orderBy(qPostLike.id.post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = jpaQueryFactory.selectFrom(qPostLike)
            .where(qPostLike.id.user.eq(user))
            .fetchCount();

        List<PostResponseDto> postResponseDtos = posts.stream()
            .map(PostResponseDto::new)
            .collect(Collectors.toList());

        return new PageImpl<>(postResponseDtos, pageable, total);
    }

    @Transactional(readOnly = true)
    public boolean PostsLikedByUser(Long postId, User user) {
        QPostLike qPostLike = QPostLike.postLike;
        return jpaQueryFactory.selectFrom(qPostLike)
            .where(qPostLike.id.post.id.eq(postId)
                .and(qPostLike.id.user.eq(user)))
            .fetchFirst() != null;
    }
}
