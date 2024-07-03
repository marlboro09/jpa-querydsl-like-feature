package com.sparta.dailyswitter.domain.like.commentlike.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.comment.dto.CommentResponseDto;
import com.sparta.dailyswitter.domain.comment.entity.Comment;
import com.sparta.dailyswitter.domain.comment.service.CommentService;
import com.sparta.dailyswitter.domain.like.commentlike.entity.CommentLike;
import com.sparta.dailyswitter.domain.like.commentlike.entity.CommentLikeId;
import com.sparta.dailyswitter.domain.like.commentlike.entity.QCommentLike;
import com.sparta.dailyswitter.domain.like.commentlike.repository.CommentLikeRepository;
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
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public void createCommentLike(Long postId, Long commentId, User user) {
        Comment comment = commentService.findById(commentId);
        Post post = postService.findById(postId);

        CommentLikeId commentLikeId = CommentLikeId.builder()
            .user(user)
            .comment(comment)
            .build();

        if (commentLikeRepository.findById(commentLikeId).isPresent()) {
            throw new CustomException(ErrorCode.COMMENT_LIKE_EXIST);
        }

        commentService.checkCommentPostNotFound(comment, post);
        commentService.checkCommentUserFound(comment, user);

        CommentLike commentLike = CommentLike.builder()
            .id(commentLikeId)
            .build();

        commentLikeRepository.save(commentLike);
        comment.addCommentLikes();
    }

    @Transactional
    public void deleteCommentLike(Long postId, Long commentId, User user) {
        Comment comment = commentService.findById(commentId);
        Post post = postService.findById(postId);

        CommentLikeId commentLikeId = CommentLikeId.builder()
            .user(user)
            .comment(comment)
            .build();

        if (commentLikeRepository.findById(commentLikeId).isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_LIKE_NOT_EXIST);
        }

        commentService.checkCommentPostNotFound(comment, post);
        commentService.checkCommentUserFound(comment, user);

        CommentLike commentLike = CommentLike.builder()
            .id(commentLikeId)
            .build();

        commentLikeRepository.delete(commentLike);
        comment.subCommentLikes();
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getLikedComments(User user, Pageable pageable) {
        QCommentLike qCommentLike = QCommentLike.commentLike;

        List<Comment> comments = jpaQueryFactory.select(qCommentLike.id.comment)
            .from(qCommentLike)
            .where(qCommentLike.id.user.eq(user))
            .orderBy(qCommentLike.id.comment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = jpaQueryFactory.selectFrom(qCommentLike)
            .where(qCommentLike.id.user.eq(user))
            .fetchCount();

        List<CommentResponseDto> commentResponseDtos = comments.stream()
            .map(CommentResponseDto::new)
            .collect(Collectors.toList());

        return new PageImpl<>(commentResponseDtos, pageable, total);
    }

    @Transactional(readOnly = true)
    public boolean CommentsLikedByUser(Long postId, User user) {
        QCommentLike qCommentLike = QCommentLike.commentLike;
        return jpaQueryFactory.selectFrom(qCommentLike)
            .where(qCommentLike.id.comment.id.eq(postId)
                .and(qCommentLike.id.user.eq(user)))
            .fetchFirst() != null;
    }
}
