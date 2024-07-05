package com.sparta.dailyswitter.domain.comment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.dailyswitter.domain.comment.dto.CommentRequestDto;
import com.sparta.dailyswitter.domain.comment.dto.CommentResponseDto;
import com.sparta.dailyswitter.domain.comment.entity.Comment;
import com.sparta.dailyswitter.domain.comment.service.CommentService;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;
import com.sparta.dailyswitter.security.UserDetailsImpl;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private UserDetailsImpl userDetails;
    private CommentRequestDto requestDto;
    private CommentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .userId("testuser")
            .username("testuser")
            .password("Password1!")
            .email("test@email.com")
            .intro("Test")
            .role(UserRoleEnum.USER)
            .isBlocked(false)
            .build();

        testPost = Post.builder()
            .title("Test Title")
            .contents("Test Post")
            .user(testUser)
            .postLikes(0L)
            .isPinned(false)
            .build();

        userDetails = new UserDetailsImpl(testUser);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        requestDto = CommentRequestDto.builder()
            .content("Test comment")
            .build();

        Comment testComment = Comment.builder()
            .id(1L)
            .user(testUser)
            .post(testPost)
            .requestDto(requestDto)
            .build();

        responseDto = CommentResponseDto.builder()
            .comment(testComment)
            .build();
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment() throws Exception {
        when(commentService.createComment(any(Long.class), any(CommentRequestDto.class),
            any(User.class)))
            .thenReturn(responseDto);

        MvcResult result = mockMvc.perform(post("/api/posts/1/comments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andReturn();

        CommentResponseDto commentResponseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), CommentResponseDto.class);

        assertEquals(responseDto.getContent(), commentResponseDto.getContent());
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void getComment() throws Exception {
        when(commentService.getComment(anyLong())).thenReturn(
            Collections.singletonList(responseDto));

        MvcResult result = mockMvc.perform(get("/api/posts/1/comments")
                .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn();

        List<CommentResponseDto> commentResponseDtos = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(
                List.class, CommentResponseDto.class));

        assertEquals(1, commentResponseDtos.size());
        assertEquals(responseDto.getContent(), commentResponseDtos.get(0).getContent());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment() throws Exception {
        when(commentService.updateComment(anyLong(), anyLong(), any(CommentRequestDto.class),
            any(User.class))).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(put("/api/posts/1/comments/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andReturn();

        CommentResponseDto commentResponseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), CommentResponseDto.class);

        assertEquals(responseDto.getContent(), commentResponseDto.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong(), any(User.class));

        MvcResult result = mockMvc.perform(delete("/api/posts/1/comments/1")
                .content("application/json"))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("댓글이 삭제되었습니다.", result.getResponse().getContentAsString());
    }
}