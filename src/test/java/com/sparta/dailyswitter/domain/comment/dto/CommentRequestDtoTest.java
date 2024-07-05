package com.sparta.dailyswitter.domain.comment.dto;

import static org.springframework.test.util.AssertionErrors.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentRequestDtoTest {

    @Test
    @DisplayName("빈 내용일 경우 테스트")
    void EmptyContents() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
            .content("")
            .build();

        assertFalse("댓글 내용을 작성해주세요", isValid(requestDto));
    }

    @Test
    @DisplayName("null 내용일 경우 테스트")
    void NullContents() {
        CommentRequestDto requestDto = CommentRequestDto.builder()
            .content(null)
            .build();

        assertFalse("댓글 내용을 작성해주세요", isValid(requestDto));
    }

    private boolean isValid(CommentRequestDto requestDto) {
        return requestDto.getContent() != null && !requestDto.getContent().isEmpty();
    }
}