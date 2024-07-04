package com.sparta.dailyswitter.security;

import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;

public class TestUserDetailsImpl {

    public static UserDetailsImpl testUserDetails() {
        User user = User.builder()
            .userId("testuser")
            .password("password")
            .email("test@email.com")
            .role(UserRoleEnum.ADMIN)
            .username("user")
            .isBlocked(false)
            .build();

        return new UserDetailsImpl(user);
    }
}
