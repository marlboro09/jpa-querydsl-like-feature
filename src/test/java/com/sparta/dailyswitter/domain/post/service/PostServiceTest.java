package com.sparta.dailyswitter.domain.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.follow.service.FollowService;
import com.sparta.dailyswitter.domain.post.dto.PostRequestDto;
import com.sparta.dailyswitter.domain.post.dto.PostResponseDto;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.post.repository.PostRepository;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowService followService;

    @InjectMocks
    private PostService postService;

    private User user;
    private Post post;
    private PostRequestDto postRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
            .id(1L)
            .userId("testuser")
            .username("user")
            .password("password")
            .email("test@email.com")
            .build();

        post = Post.builder()
            .id(1L)
            .title("Title")
            .contents("Contents")
            .user(user)
            .build();

        postRequestDto = PostRequestDto.builder()
            .title("Title")
            .contents("Contents")
            .build();
    }

    @Test
    @DisplayName("게시물 생성 성공 테스트")
    void createPost() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDto responseDto = postService.createPost(postRequestDto, "testuser");

        assertNotNull(responseDto);
        assertEquals("Title", responseDto.getTitle());
        assertEquals("Contents", responseDto.getContents());
        assertEquals("testuser", responseDto.getUserId());
    }


    @Test
    @DisplayName("게시물 생성 시 비회원일 경우 예외발생")
    void createPost_UserNotFound() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
            postService.createPost(postRequestDto, "testuser"));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 수정 성공 테스트")
    void updatePost() {
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Updated Title")
            .contents("Updated Contents")
            .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDto updatedPost = postService.updatePost(1L, requestDto, "testuser");

        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Contents", updatedPost.getContents());
    }

    @Test
    @DisplayName("없는 게시물을 수정할 때 예외발생")
    void updatePost_PostNotFound() {
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Updated Title")
            .contents("Updated Contents")
            .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            postService.updatePost(1L, requestDto, "testuser");
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("작성자가 아닐 때 수정할 시 예외발생")
    void updatePost_UserMismatch() {
        PostRequestDto requestDto = PostRequestDto.builder()
            .title("Updated Title")
            .contents("Updated Contents")
            .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        CustomException exception = assertThrows(CustomException.class, () -> {
            postService.updatePost(1L, requestDto, "anotheruser");
        });

        assertEquals(ErrorCode.POST_NOT_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 삭제 성공 테스트")
    void deletePost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        postService.deletePost(1L, "testuser");

        verify(postRepository, times(1)).delete(post);
    }

    @Test
    @DisplayName("없는 게시물 삭제 시 예외발생")
    void deletePost_PostNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            postService.deletePost(1L, "testuser");
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 삭제 시 사용자가 불일치할 경우 예외발생")
    void deletePost_UserMismatch() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        CustomException exception = assertThrows(CustomException.class, () -> {
            postService.deletePost(1L, "anotheruser");
        });

        assertEquals(ErrorCode.POST_NOT_USER, exception.getErrorCode());
    }
}