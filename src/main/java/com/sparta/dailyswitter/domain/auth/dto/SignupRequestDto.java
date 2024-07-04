package com.sparta.dailyswitter.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupRequestDto {
	@Size(min = 4, max = 10, message = "ID는 최소 4자 이상 10자 이하입니다.")
	@Pattern(regexp = "^[a-z0-9]{4,10}$", message = "ID는 알파벳 소문자와 숫자만 사용하실 수 있습니다.")
	@NotBlank
	private String userId;

	@Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상 15자 이하입니다.")
	@Pattern(
		regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$",
		message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 최소 1글자씩 포함해야 합니다."
	)
	@NotBlank
	private String password;

	@NotBlank
	private String username;

	@Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")
	@NotBlank
	private String email;

	private String intro;

	@Builder.Default
	private boolean admin = false;

	@Builder.Default
	private String adminToken = "";
}
