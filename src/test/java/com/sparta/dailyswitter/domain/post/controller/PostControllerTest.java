package com.sparta.dailyswitter.domain.post.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.dailyswitter.domain.post.dto.PostRequestDto;
import com.sparta.dailyswitter.domain.post.dto.PostResponseDto;
import com.sparta.dailyswitter.domain.post.service.PostService;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;
import com.sparta.dailyswitter.security.UserDetailsImpl;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    private UserDetailsImpl userDetails;
    private User testUser;
    private PostRequestDto postRequestDto;

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

        userDetails = new UserDetailsImpl(testUser);

        postRequestDto = PostRequestDto.builder()
            .title("Test Title")
            .contents("Test Contents")
            .build();

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @Test
    @DisplayName("게시물 생성 성공 테스트")
    @WithMockUser(username = "testuser")
    public void createPost_Success() throws Exception {
        when(postService.createPost(any(PostRequestDto.class), any(String.class))).thenReturn(
            PostResponseDto.builder()
                .title(postRequestDto.getTitle())
                .contents(postRequestDto.getContents())
                .userId(testUser.getUsername())
                .postLikes(0L)
                .isPinned(false)
                .createdAt(null)
                .updatedAt(null)
                .build());

        MvcResult result = mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(postRequestDto)))
            .andExpect(status().isOk())
            .andReturn();

        PostResponseDto responseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), PostResponseDto.class);
        assertEquals(postRequestDto.getTitle(), responseDto.getTitle());
        assertEquals(postRequestDto.getContents(), responseDto.getContents());
        assertEquals(testUser.getUsername(), responseDto.getUserId());
    }

    @Test
    @DisplayName("게시물 수정 테스트")
    @WithMockUser(username = "testuser")
    public void updatePost() throws Exception {
        PostRequestDto updatedRequestDto = PostRequestDto.builder()
            .title("Updated Title")
            .contents("Updated Contents")
            .build();

        when(postService.updatePost(anyLong(), any(PostRequestDto.class),
            any(String.class))).thenReturn(
            PostResponseDto.builder()
                .title(updatedRequestDto.getTitle())
                .contents(updatedRequestDto.getContents())
                .userId(testUser.getUserId())
                .postLikes(0L)
                .isPinned(false)
                .createdAt(null)
                .updatedAt(null)
                .build());

        MvcResult result = mockMvc.perform(put("/api/posts/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updatedRequestDto)))
            .andExpect(status().isOk())
            .andReturn();

        PostResponseDto responseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), PostResponseDto.class);
        assertEquals(updatedRequestDto.getTitle(), responseDto.getTitle());
        assertEquals(updatedRequestDto.getContents(), responseDto.getContents());
    }

    @Test
    @DisplayName("게시물 삭제 성공 테스트")
    @WithMockUser(username = "testuser")
    public void deletePost() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("게시물이 삭제되었습니다.", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("게시물 조회 성공 테스트")
    @WithMockUser(username = "testuser")
    public void getPost() throws Exception {
        when(postService.getPost(anyLong())).thenReturn(
            PostResponseDto.builder()
                .title(postRequestDto.getTitle())
                .contents(postRequestDto.getContents())
                .userId(testUser.getUsername())
                .postLikes(0L)
                .isPinned(false)
                .createdAt(null)
                .updatedAt(null)
                .build());

        MvcResult result = mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isOk())
            .andReturn();

        PostResponseDto responseDto = objectMapper.readValue(
            result.getResponse().getContentAsString(), PostResponseDto.class);
        assertEquals(postRequestDto.getTitle(), responseDto.getTitle());
        assertEquals(postRequestDto.getContents(), responseDto.getContents());
        assertEquals(testUser.getUserId(), responseDto.getUserId());
    }

    /**
     * pageable쪽의 문제로 작동이 안됩니다.
     */
    @Test
    @DisplayName("팔로우한 게시물 조회 테스트")
    @WithMockUser(username = "testuser")
    public void getFollowingPosts() throws Exception {
        PostResponseDto postResponseDto = PostResponseDto.builder()
            .title("Test Title")
            .contents("Test Contents")
            .userId(testUser.getUsername())
            .postLikes(0L)
            .isPinned(false)
            .createdAt(null)
            .updatedAt(null)
            .build();

        Page<PostResponseDto> page = new PageImpl<>(Collections.singletonList(postResponseDto),
            PageRequest.of(0, 5), 1);

        when(postService.getFollowedPosts(any(User.class), any(Pageable.class))).thenReturn(page);

        MvcResult result = mockMvc.perform(get("/api/posts/following")
                .param("page", "0"))
            .andExpect(status().isOk())
            .andReturn();

        PageImpl<PostResponseDto> responsePage = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<PageImpl<PostResponseDto>>() {
            });

        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1, responsePage.getTotalPages());
        assertEquals(postResponseDto.getTitle(), responsePage.getContent().get(0).getTitle());
    }
}