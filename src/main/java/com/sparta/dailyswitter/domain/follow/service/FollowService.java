package com.sparta.dailyswitter.domain.follow.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.dailyswitter.common.exception.CustomException;
import com.sparta.dailyswitter.common.exception.ErrorCode;
import com.sparta.dailyswitter.domain.follow.entity.Follow;
import com.sparta.dailyswitter.domain.follow.entity.QFollow;
import com.sparta.dailyswitter.domain.follow.repository.FollowRepository;
import com.sparta.dailyswitter.domain.user.entity.User;
import com.sparta.dailyswitter.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public void followUser(Long followerUserId, Long followingUserId) {
        User followerUser = userRepository.findById(followerUserId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        User followingUser = userRepository.findById(followingUserId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (followRepository.findByFollowerUserAndFollowingUser(followerUser, followingUser)
            == null) {
            Follow follow = Follow.builder()
                .followerUser(followerUser)
                .followingUser(followingUser)
                .build();
            followRepository.save(follow);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
    }

    @Transactional
    public void unfollowUser(Long followerUserId, Long followingUserId) {
        User followerUser = userRepository.findById(followerUserId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        User followingUser = userRepository.findById(followingUserId).orElseThrow(
            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Follow follow = followRepository.findByFollowerUserAndFollowingUser(followerUser,
            followingUser);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<User> getFollows(User followerUser) {
        QFollow qFollow = QFollow.follow;
        List<Follow> follows = jpaQueryFactory.selectFrom(qFollow)
            .where(qFollow.followerUser.eq(followerUser))
            .fetch();

        if (follows.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return follows.stream().map(Follow::getFollowingUser).collect(Collectors.toList());
    }
}