package com.sparta.dailyswitter.domain.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.dailyswitter.domain.auth.dto.LoginRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.LoginResponseDto;
import com.sparta.dailyswitter.domain.auth.dto.SignoutRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.SignupRequestDto;
import com.sparta.dailyswitter.domain.auth.service.AuthService;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;
import com.sparta.dailyswitter.security.UserDetailsImpl;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private Principal mockPrincipal;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .userId("testuser")
            .password("Password1!")
            .username("testuser")
            .email("test@email.com")
            .intro("Test")
            .role(UserRoleEnum.USER)
            .isBlocked(false)
            .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, testUser.getPassword(),
            userDetails.getAuthorities());
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void testSignup() throws Exception {
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("Password123!")
            .username("testuser")
            .email("test@email.com")
            .intro("Test")
            .admin(false)
            .adminToken("")
            .build();

        when(authService.signup(any(SignupRequestDto.class))).thenReturn(testUser);

        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequestDto)))
            .andExpect(status().isOk())
            .andReturn();

        User responseUser = objectMapper.readValue(result.getResponse().getContentAsString(),
            User.class);
        assertEquals(testUser.getId(), responseUser.getId());
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        assertEquals(testUser.getEmail(), responseUser.getEmail());
    }

    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("testuser", "password");

        LoginResponseDto loginResponseDto = new LoginResponseDto("token", "refreshtoken",
            "message");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isOk())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        LoginResponseDto responseDto = objectMapper.readValue(jsonResponse, LoginResponseDto.class);
        assertEquals(loginResponseDto.getToken(), responseDto.getToken());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    public void testLogout() throws Exception {
        doNothing().when(authService).logout(any(User.class));

        MvcResult result = mockMvc.perform(put("/api/auth/logout")
                .with(user(new UserDetailsImpl(testUser))))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("로그아웃이 완료되었습니다.", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    public void testSignout() throws Exception {
        SignoutRequestDto signoutRequestDto = new SignoutRequestDto("password");

        doNothing().when(authService).signout(any(SignoutRequestDto.class), any(User.class));

        MvcResult result = mockMvc.perform(put("/api/auth/signout")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signoutRequestDto))
                .with(user(new UserDetailsImpl(testUser))))
            .andExpect(status().isOk())
            .andReturn();

        assertEquals("탈퇴가 완료되었습니다.", result.getResponse().getContentAsString());
    }
}
