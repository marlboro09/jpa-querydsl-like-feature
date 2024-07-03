package com.sparta.dailyswitter.domain.post.dto;

import com.sparta.dailyswitter.domain.post.entity.Post;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponseDto {
	private final String title;
	private final String contents;
	private final String userId;
	private Long postLikes;
	private final boolean isPinned;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@Builder
	public PostResponseDto(String title, String contents, String userId, Long postLikes, boolean isPinned, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.title = title;
		this.contents = contents;
		this.userId = userId;
		this.postLikes = postLikes;
		this.isPinned = isPinned;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public PostResponseDto(Post post) {
		this.title = post.getTitle();
		this.contents = post.getContents();
		this.userId = post.getUser().getUsername();
		this.postLikes = post.getPostLikes();
		this.isPinned = post.isPinned();
		this.createdAt = post.getCreatedAt();
		this.updatedAt = post.getUpdatedAt();
	}
}
