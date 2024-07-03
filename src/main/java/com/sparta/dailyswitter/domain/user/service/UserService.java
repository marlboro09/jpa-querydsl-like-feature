package com.sparta.dailyswitter.domain.user.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.like.commentlike.entity.QCommentLike;
import com.sparta.dailyswitter.domain.like.postlike.entity.QPostLike;
import com.sparta.dailyswitter.domain.user.dto.UserInfoRequestDto;
import com.sparta.dailyswitter.domain.user.dto.UserPwRequestDto;
import com.sparta.dailyswitter.domain.user.dto.UserResponseDto;
import com.sparta.dailyswitter.domain.user.dto.UserRoleChangeRequestDto;
import com.sparta.dailyswitter.domain.user.entity.QUser;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory jpaQueryFactory;

    public UserResponseDto getUser(Long id) {
        User user = findUserById(id);
        return createUserResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        QUser qUser = QUser.user;
        List<User> users = jpaQueryFactory
            .selectFrom(qUser)
            .fetch();

        return users.stream()
            .map(this::createUserResponseDto)
            .collect(Collectors.toList());
    }

    public UserResponseDto updateUserInfo(Long id, UserInfoRequestDto userInfoRequestDto) {
        User user = findUserById(id);

        user.updateUserInfo(userInfoRequestDto);
        userRepository.save(user);

        return createUserResponseDto(user);
    }

    public UserResponseDto updatePassword(Long id, UserPwRequestDto userPwRequestDto) {
        User user = findUserById(id);

        if (!passwordEncoder.matches(userPwRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (user.isPasswordInHistory(userPwRequestDto.getNewPassword(), passwordEncoder)) {
            throw new CustomException(ErrorCode.DUPLICATE_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(userPwRequestDto.getNewPassword()));
        userRepository.save(user);

        return createUserResponseDto(user);
    }

    public UserResponseDto updateUserRole(Long id,
        UserRoleChangeRequestDto userRoleChangeRequestDto) {
        User user = findUserById(id);
        user.updateStatus(userRoleChangeRequestDto.getRole());
        userRepository.save(user);

        return createUserResponseDto(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDto toggleBlockStatus(Long id) {
        User user = findUserById(id);
        user.toggleBlock();
        userRepository.save(user);

        return createUserResponseDto(user);
    }

    private User findUserById(Long id) {
        QUser qUser = QUser.user;
        User user = jpaQueryFactory
            .selectFrom(qUser)
            .where(qUser.id.eq(id))
            .fetchOne();

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private UserResponseDto createUserResponseDto(User user) {
        Long likedPostsCount = getLikedPostsCount(user);
        Long likedCommentsCount = getLikedCommentsCount(user);
        return new UserResponseDto(user, likedPostsCount, likedCommentsCount);
    }

    private Long getLikedPostsCount(User user) {
        QPostLike qPostLike = QPostLike.postLike;
        return jpaQueryFactory
            .select(qPostLike.count())
            .from(qPostLike)
            .where(qPostLike.id.user.eq(user))
            .fetchOne();
    }

    private Long getLikedCommentsCount(User user) {
        QCommentLike qCommentLike = QCommentLike.commentLike;
        return jpaQueryFactory
            .select(qCommentLike.count())
            .from(qCommentLike)
            .where(qCommentLike.id.user.eq(user))
            .fetchOne();
    }
}

