package com.sparta.dailyswitter.domain.like.commentlike.controller;

import com.sparta.dailyswitter.domain.comment.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dailyswitter.domain.like.commentlike.service.CommentLikeService;
import com.sparta.dailyswitter.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PostMapping("/posts/{postId}/comments/{commentId}/likes")
	public ResponseEntity<String> createCommentLikes(@PathVariable Long postId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		commentLikeService.createCommentLike(postId, commentId, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK)
			.body("댓글 좋아요가 등록되었습니다.");
	}

	@DeleteMapping("/posts/{postId}/comments/{commentId}/likes")
	public ResponseEntity<String> deleteCommentLikes(@PathVariable Long postId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		commentLikeService.deleteCommentLike(postId, commentId, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK)
			.body("댓글 좋아요가 삭제되었습니다.");
	}

	@GetMapping("/comments/likes")
	public ResponseEntity<Page<CommentResponseDto>> getLikeComments(@AuthenticationPrincipal
		UserDetailsImpl userDetails, Pageable pageable) {
		Page<CommentResponseDto> likedComment = commentLikeService.getLikeComments(userDetails.getUser(), pageable);
		return ResponseEntity.status(HttpStatus.OK).body(likedComment);
	}
}
