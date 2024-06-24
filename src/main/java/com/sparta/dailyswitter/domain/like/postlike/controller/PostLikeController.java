package com.sparta.dailyswitter.domain.like.postlike.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.dailyswitter.domain.like.postlike.service.PostLikeService;
import com.sparta.dailyswitter.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/posts/{postId}/likes")
	public ResponseEntity<String> createPostLikes(@PathVariable Long postId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		postLikeService.createPostLike(postId, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK)
			.body("게시물 좋아요가 등록되었습니다.");
	}

	@DeleteMapping("/posts/{postId}/likes")
	public ResponseEntity<String> deletePostLikes(@PathVariable Long postId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		postLikeService.deletePostLike(postId, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.OK)
			.body("게시물 좋아요가 삭제되었습니다.");
	}
}
