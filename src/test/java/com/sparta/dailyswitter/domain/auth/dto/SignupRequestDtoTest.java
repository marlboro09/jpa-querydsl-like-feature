package com.sparta.dailyswitter.domain.auth.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignupRequestDtoTest {

    @Test
    @DisplayName("유효한 singup 테스트")
    void validSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("validuser")
            .password("Valid123!")
            .username("Valid User")
            .email("valid@example.com")
            .intro("Valid intro")
            .build();

        assertTrue(isValid(requestDto));
    }

    @Test
    @DisplayName("유효하지 않은 userId 테스트")
    void invalidUserIdSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("test_user!")
            .password("Password123!")
            .username("user")
            .email("test@email.com")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("유효하지 않은 password 테스트")
    void invalidPasswordSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("password")
            .username("user")
            .email("test@email.com")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("유효하지 않은 email 테스트")
    void invalidEmailSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("Password123!")
            .username("user")
            .email("invalid-email")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("빈 userId 테스트")
    void blankUserIdSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("")
            .password("Password123!")
            .username("user")
            .email("test@email.com")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("빈 password 테스트")
    void blankPasswordSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("")
            .username("user")
            .email("test@email.com")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("빈 username 테스트")
    void blankUsernameSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("Password123!")
            .username("")
            .email("test@email.com")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    @Test
    @DisplayName("빈 email 테스트")
    void blankEmailSignupRequestDto() {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .userId("testuser")
            .password("password123!")
            .username("user")
            .email("")
            .intro("Test")
            .build();

        assertFalse(isValid(requestDto));
    }

    private boolean isValid(SignupRequestDto requestDto) {
        if (requestDto.getUserId() == null || !requestDto.getUserId().matches("^[a-z0-9]{4,10}$")) {
            return false;
        }
        if (requestDto.getPassword() == null || !requestDto.getPassword()
            .matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$")) {
            return false;
        }
        if (requestDto.getUsername() == null || requestDto.getUsername().isBlank()) {
            return false;
        }
        if (requestDto.getEmail() == null || !requestDto.getEmail().matches(
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")) {
            return false;
        }
        return true;
    }
}