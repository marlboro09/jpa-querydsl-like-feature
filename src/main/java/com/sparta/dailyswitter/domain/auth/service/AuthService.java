package com.sparta.dailyswitter.domain.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.auth.dto.LoginRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.LoginResponseDto;
import com.sparta.dailyswitter.domain.auth.dto.SignoutRequestDto;
import com.sparta.dailyswitter.domain.auth.dto.SignupRequestDto;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.entity.UserRoleEnum;
import com.sparta.dailyswitter.domain.user.repository.UserRepository;
import com.sparta.dailyswitter.security.JwtUtil;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	@Value("${admin.token:default-admin-token}")
	private String adminToken;

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public User signup(SignupRequestDto requestDto) throws IOException {
		String userId = requestDto.getUserId();
		String password = passwordEncoder.encode(requestDto.getPassword());

		Optional<User> checkUser = userRepository.findByUserId(userId);
		if (checkUser.isPresent()) {
			throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
		}

		String email = requestDto.getEmail();
		Optional<User> checkEmail = userRepository.findByEmail(email);
		if (checkEmail.isPresent()) {
			throw new CustomException(ErrorCode.EMAIL_NOT_UNIQUE);
		}

		UserRoleEnum role = UserRoleEnum.USER;
		if (requestDto.isAdmin()) {
			if (requestDto.getAdminToken() == null || !adminToken.equals(requestDto.getAdminToken())) {
				throw new CustomException(ErrorCode.INCORRECT_ADMIN_KEY);
			}
			role = UserRoleEnum.ADMIN;
		}

		User user = User.builder()
			.userId(userId)
			.password(password)
			.username(requestDto.getUsername())
			.email(requestDto.getEmail())
			.intro(requestDto.getIntro())
			.role(role)
			.build();

		return userRepository.save(user);
	}

	@Transactional
	public LoginResponseDto login(@RequestBody LoginRequestDto requestDto) {
		User user = this.userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
			() -> new CustomException(ErrorCode.USER_NOT_FOUND)
		);

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(requestDto.getUserId(), requestDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtUtil.createToken(requestDto.getUserId());
		String refreshToken = jwtUtil.createRefreshToken(requestDto.getUserId());
		user.updateRefresh(refreshToken);
		userRepository.save(user);

		return new LoginResponseDto(token, refreshToken, "로그인에 성공했습니다.");
	}

	public void logout(User user) {
		user.updateRefresh(null);
		userRepository.save(user);
	}

	public void signout(SignoutRequestDto requestDto, User user) {
		if (!user.isExist()) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		String password = requestDto.getPassword();
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
		}

		user.updateStatusSignout();
		userRepository.save(user);
	}
}
