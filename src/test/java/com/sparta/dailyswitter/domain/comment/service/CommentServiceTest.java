package com.sparta.dailyswitter.domain.comment.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.comment.dto.CommentRequestDto;
import com.sparta.dailyswitter.domain.comment.dto.CommentResponseDto;
import com.sparta.dailyswitter.domain.comment.entity.Comment;
import com.sparta.dailyswitter.domain.comment.entity.QComment;
import com.sparta.dailyswitter.domain.comment.repository.CommentRepository;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.post.service.PostService;
import com.sparta.dailyswitter.domain.user.entity.User;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CommentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .userId("testuser")
            .build();

        testPost = Post.builder()
            .id(1L)
            .title("Post")
            .build();

        testComment = Comment.builder()
            .id(1L)
            .user(testUser)
            .post(testPost)
            .requestDto(CommentRequestDto.builder().content("Test Comment").build())
            .build();

        requestDto = CommentRequestDto.builder()
            .content("New Comment")
            .build();
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    public void createComment_Success() {
        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponseDto responseDto = commentService.createComment(testPost.getId(), requestDto,
            testUser);

        assertNotNull(responseDto);
        assertEquals("New Comment", responseDto.getContent());
    }

    @Test
    @DisplayName("게시글 댓글 조회 성공 테스트")
    public void getComment_Success() {
        QComment qComment = QComment.comment;
        JPAQuery<Comment> query = mock(JPAQuery.class);

        when(jpaQueryFactory.selectFrom(qComment)).thenReturn(query);
        when(query.where(qComment.post.id.eq(testPost.getId()))).thenReturn(query);
        when(query.fetch()).thenReturn(Collections.singletonList(testComment));

        List<CommentResponseDto> comments = commentService.getComment(testPost.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Test Comment", comments.get(0).getContent());
    }

    @Test
    @DisplayName("댓글 조회 시 댓글이 없을 경우 예외발생")
    public void getComment_Fail() {
        QComment qComment = QComment.comment;
        JPAQuery<Comment> query = mock(JPAQuery.class);

        when(jpaQueryFactory.selectFrom(qComment)).thenReturn(query);
        when(query.where(qComment.post.id.eq(testPost.getId()))).thenReturn(query);
        when(query.fetch()).thenReturn(Collections.emptyList());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.getComment(testPost.getId());
        });

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 업데이트 성공 테스트")
    public void updateComment() {
        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        CommentResponseDto responseDto = commentService.updateComment(testPost.getId(),
            testComment.getId(), requestDto, testUser);

        assertNotNull(responseDto);
        assertEquals("New Comment", responseDto.getContent());
    }

    @Test
    @DisplayName("댓글 업데이트 시 댓글이 없을 경우 예외발생")
    public void updateComment_CommentNotFound() {
        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.updateComment(testPost.getId(), testComment.getId(), requestDto,
                testUser);
        });

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("작성자가 아닐 때 댓글 업데이트를 할 경우 예외발생")
    public void updateComment_NotUser() {
        User otherUser = User.builder()
            .id(2L)
            .userId("otheruser")
            .build();

        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.updateComment(testPost.getId(), testComment.getId(), requestDto,
                otherUser);
        });

        assertEquals(ErrorCode.COMMENT_NOT_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    public void deleteComment() {
        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        assertDoesNotThrow(() -> {
            commentService.deleteComment(testPost.getId(), testComment.getId(), testUser);
        });
    }

    @Test
    @DisplayName("댓글 삭제 시 댓글이 없는 경우 예외발생")
    public void deleteComment_NotFoundComment() {
        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.deleteComment(testPost.getId(), testComment.getId(), testUser);
        });

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 시 작성자가 아닌 경우 예외발생")
    public void deleteComment_CommentNotUser() {
        User otheruser = User.builder()
            .id(2L)
            .userId("otheruser")
            .build();

        when(postService.findById(anyLong())).thenReturn(testPost);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.deleteComment(testPost.getId(), testComment.getId(), otheruser);
        });

        assertEquals(ErrorCode.COMMENT_NOT_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("관리자 댓글 업데이트 성공 테스트")
    public void adminUpdateComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        CommentResponseDto responseDto = commentService.adminUpdateComment(testComment.getId(),
            requestDto);

        assertNotNull(responseDto);
        assertEquals("New Comment", responseDto.getContent());
    }

    @Test
    @DisplayName("관리자 댓글 삭제 성공 테스트")
    public void adminDeleteComment() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        assertDoesNotThrow(() -> {
            commentService.adminDeleteComment(testComment.getId());
        });
    }
}