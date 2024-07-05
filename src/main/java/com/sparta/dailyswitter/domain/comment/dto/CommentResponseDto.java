package com.sparta.dailyswitter.domain.comment.dto;

import com.sparta.dailyswitter.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
	private String postTitle;
	private String content;
	private String userId;
	private Long commentLikes;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	public CommentResponseDto(Comment comment) {
		this.postTitle = comment.getPost().getTitle();
		this.content = comment.getContent();
		this.userId = comment.getUser().getUserId();
		this.commentLikes = comment.getCommentLikes();
		this.createdAt = comment.getCreatedAt();
		this.updatedAt = comment.getUpdatedAt();
	}
}