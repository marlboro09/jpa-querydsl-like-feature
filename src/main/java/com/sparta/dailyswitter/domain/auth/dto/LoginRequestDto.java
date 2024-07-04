package com.sparta.dailyswitter.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

	private String userId;
	private String password;

	@Builder
	public LoginRequestDto(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}
}
