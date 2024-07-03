package com.sparta.dailyswitter.domain.comment.service;

import static com.sparta.dailyswitter.common.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.sparta.dailyswitter.common.exception.ErrorCode.COMMENT_NOT_USER;
import static com.sparta.dailyswitter.common.exception.ErrorCode.COMMENT_SAME_USER;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.domain.comment.dto.CommentRequestDto;
import com.sparta.dailyswitter.domain.comment.dto.CommentResponseDto;
import com.sparta.dailyswitter.domain.comment.entity.Comment;
import com.sparta.dailyswitter.domain.comment.entity.QComment;
import com.sparta.dailyswitter.domain.comment.repository.CommentRepository;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.post.service.PostService;
import com.sparta.dailyswitter.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostService postService;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, User user) {
        Post post = postService.findById(postId);
        Comment comment = Comment.builder()
            .user(user)
            .post(post)
            .requestDto(requestDto)
            .build();

        commentRepository.save(comment);
        return CommentResponseDto.builder()
            .comment(comment)
            .build();
    }

    public List<CommentResponseDto> getComment(Long postId) {
        QComment qComment = QComment.comment;
        BooleanExpression booleanExpression = qComment.post.id.eq(postId);
        List<Comment> commentList = jpaQueryFactory.selectFrom(qComment)
            .where(booleanExpression)
            .fetch();

        if (commentList.isEmpty()) {
            throw new CustomException(COMMENT_NOT_FOUND);
        }
        return commentList.stream()
            .map(comment -> CommentResponseDto.builder()
                .comment(comment)
                .build())
            .toList();
    }

    public List<CommentResponseDto> getAllComments() {
        QComment qComment = QComment.comment;
        List<Comment> comments = jpaQueryFactory.selectFrom(qComment)
            .orderBy(qComment.createdAt.desc())
            .fetch();

        return comments.stream()
            .map(CommentResponseDto::new)
            .toList();
    }

    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId,
        CommentRequestDto requestDto, User user) {
        Post post = postService.findById(postId);
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        checkCommentPostNotFound(comment, post);
        checkCommentUserNotFound(comment, user);
        comment.updateComment(requestDto);

        return CommentResponseDto.builder()
            .comment(comment)
            .build();
    }

    @Transactional
    public CommentResponseDto adminUpdateComment(Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        comment.updateComment(requestDto);

        return CommentResponseDto.builder()
            .comment(comment)
            .build();
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {
        Post post = postService.findById(postId);
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        checkCommentPostNotFound(comment, post);
        checkCommentUserNotFound(comment, user);
        commentRepository.delete(comment);
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND)
            );
    }

    public void checkCommentPostNotFound(Comment comment, Post post) {
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CustomException(COMMENT_NOT_FOUND);
        }
    }

    public void checkCommentUserNotFound(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(COMMENT_NOT_USER);
        }
    }

    public void checkCommentUserFound(Comment comment, User user) {
        if (comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(COMMENT_SAME_USER);
        }
    }

    @Transactional
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        commentRepository.delete(comment);
    }
}