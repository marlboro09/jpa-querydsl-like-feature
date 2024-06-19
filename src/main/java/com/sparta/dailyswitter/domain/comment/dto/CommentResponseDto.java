package com.sparta.dailyswitter.domain.comment.dto;

import java.time.LocalDateTime;

import com.sparta.dailyswitter.domain.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentResponseDto {
	private long id;
	private String postTitle;
	private String content;
	private String userId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	public CommentResponseDto(Comment comment) {
		this.postTitle = comment.getPost().getPostId();
		this.content = comment.getContent();
		this.userId = comment.getUser().getUserId();
		this.createdAt = comment.getCreatedAt();
		this.updatedAt = comment.getUpdatedAt();
	}
}