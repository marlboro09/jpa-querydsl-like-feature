package com.sparta.dailyswitter.domain.user.dto;

import java.time.LocalDateTime;

import com.sparta.dailyswitter.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
	private String userId;
	private String username;
	private String email;
	private String intro;
	private String role;
	private boolean isBlocked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long likedPostsCount;
	private Long likedCommentsCount;

	public UserResponseDto(User user) {
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.intro = user.getIntro();
		this.role = user.getRole().getAuthority();
		this.isBlocked = user.isBlocked();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
	}

	public UserResponseDto(User user, Long likedPostsCount, Long likedCommentsCount) {
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.intro = user.getIntro();
		this.role = user.getRole().getAuthority();
		this.isBlocked = user.isBlocked();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
		this.likedPostsCount = likedPostsCount;
		this.likedCommentsCount = likedCommentsCount;
	}
}
