package com.sns.room.follow.service;

import com.sns.room.follow.entity.Follow;
import com.sns.room.follow.repository.FollowRepository;
import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.user.entity.User;
import com.sns.room.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final AuthService authService;

    @Transactional
    public void createFollow(User fromUser, Long toUserId) {

        if (fromUser.getId().equals(toUserId)) {
            throw new InvalidInputException("자신을 팔로우할 수 없습니다.");
        }

        authService.findUser(toUserId);

        Follow follow = Follow.builder()
                .fromUserId(fromUser.getId())
                .toUserId(toUserId)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void deleteFollow(User fromUser, Long toUserId) {
        authService.findUser(toUserId);
        Follow follow = followRepository.findByFromUserIdAndToUserId(fromUser.getId(), toUserId)
                .orElseThrow(
                        () -> new InvalidInputException("해당 팔로우를 찾을 수 없습니다.")
                );
        followRepository.delete(follow);
    }
}
