package com.sparta.dailyswitter.domain.comment.entity;

import com.sparta.dailyswitter.common.util.Timestamped;
import com.sparta.dailyswitter.domain.comment.dto.CommentRequestDto;
import com.sparta.dailyswitter.domain.post.entity.Post;
import com.sparta.dailyswitter.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "postId")
	private Post post;

	@Column
	private String content;

	@Column
	private Long commentLikes;

	@Builder
	public Comment(Long id, User user, Post post, CommentRequestDto requestDto) {
		this.id = id;
		this.user = user;
		this.post = post;
		this.content = requestDto.getContent();
		this.commentLikes = 0L;
	}

	public void updateComment(CommentRequestDto requestDto) {
		this.content = requestDto.getContent();
	}

	public void addCommentLikes() {
		this.commentLikes = commentLikes + 1L;
	}

	public void subCommentLikes() {
		this.commentLikes = commentLikes - 1L;
	}
}
