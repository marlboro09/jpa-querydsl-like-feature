package com.sparta.dailyswitter.domain.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequestDto {
	@NotEmpty(message = "댓글 내용을 작성해주세요.")
	private String content;

	@Builder
	public CommentRequestDto(String content) {
		this.content = content;
	}
}
