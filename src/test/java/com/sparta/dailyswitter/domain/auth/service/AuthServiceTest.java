package com.sparta.dailyswitter.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.auth.dto.LoginRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.LoginResponseDto;
import com.sparta.dailyswitter.domain.auth.dto.SignoutRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.SignupRequestDto;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;
import com.sparta.dailyswitter.domain.user.repository.UserRepository;
import com.sparta.dailyswitter.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(authService, "adminToken",
            "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC");
    }

    @Test
    @DisplayName("사용자 ID가 중복일 때 예외 발생")
    void signup_UserIdAlready_Exception() {

        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .email("test@email.com")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.of(
            User.builder().build()));

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(requestDto));

        assertEquals(ErrorCode.USER_NOT_UNIQUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("이메일 중복일 때 예외 발생")
    void signup_EmailAlready_Exception() {

        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .email("test@email.com")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(
            Optional.of(User.builder()
                .build()));

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(requestDto));

        assertEquals(ErrorCode.EMAIL_NOT_UNIQUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("ADMIN 토큰이 잘못 되었을 때 예외 발생")
    void signup_InvalidAdminToken_Exception() {

        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("password")
            .email("test@email.com")
            .admin(true)
            .adminToken("WrongAdminToken")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(requestDto));

        assertEquals(ErrorCode.INCORRECT_ADMIN_KEY, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 - 유효한 요청일 때 사용자 반환")
    void signup_ValidRequest() throws Exception {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("password")
            .email("test@example.com")
            .username("Test User")
            .intro("Intro")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(
            invocation -> invocation.getArgument(0, User.class));

        User user = authService.signup(requestDto);

        assertNotNull(user);
        assertEquals(requestDto.getUserId(), user.getUserId());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(requestDto.getEmail(), user.getEmail());
        assertEquals(UserRoleEnum.USER, user.getRole());
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 로그인 시")
    void login_UserNotFound_Exception() {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .userId("testuser")
            .password("password")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.login(requestDto));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("유효한 로그인")
    void login_ValidRequest() {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .userId("testuser")
            .password("password")
            .build();

        User user = User.builder()
            .userId("testuser")
            .password("password")
            .build();

        when(userRepository.findByUserId(requestDto.getUserId())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(new UsernamePasswordAuthenticationToken(requestDto.getUserId(),
                requestDto.getPassword()));
        when(jwtUtil.createToken(requestDto.getUserId())).thenReturn("token");
        when(jwtUtil.createRefreshToken(requestDto.getUserId())).thenReturn("refreshToken");

        LoginResponseDto responseDto = authService.login(requestDto);

        assertNotNull(responseDto);
        assertEquals("token", responseDto.getToken());
        assertEquals("refreshToken", responseDto.getRefreshToken());
        assertEquals("로그인에 성공했습니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("유효한 로그아웃")
    void logout_ValidRequest() {
        User user = User.builder()
            .userId("testuser")
            .password("password")
            .build();

        authService.logout(user);

        assertNull(user.getRefreshToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("회원탈퇴 시 존재하지 않는 사용자일 때 예외발생")
    void signout_UserNotFound_Exception() {
        SignoutRequestDto requestDto = SignoutRequestDto.builder()
            .password("password")
            .build();

        User user = User.builder()
            .userId("testuser")
            .password("encodedpassword")
            .role(UserRoleEnum.WITHDRAW)
            .build();

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signout(requestDto, user));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원탈퇴 시 비밀번호가 틀릴 경우 예외발생")
    void signout_InvalidPassword_Exception() {
        SignoutRequestDto requestDto = SignoutRequestDto.builder()
            .password("wrongpassword")
            .build();

        User user = User.builder()
            .userId("testuser")
            .password("encodedPassword")
            .role(UserRoleEnum.USER)
            .build();

        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(
            false);

        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signout(requestDto, user));

        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원탈퇴 - 유효한 요청일 때 성공")
    void signout_ValidRequest_Success() {
        SignoutRequestDto requestDto = SignoutRequestDto.builder()
            .password("password")
            .build();

        User user = User.builder()
            .userId("testuser")
            .password("encodedPassword")
            .role(UserRoleEnum.USER)
            .build();

        when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(
            true);

        authService.signout(requestDto, user);

        assertEquals(UserRoleEnum.WITHDRAW, user.getRole());
        verify(userRepository, times(1)).save(user);
    }
}