package com.sns.room.follow.service;

import com.sns.room.follow.dto.FollowerResponseDto;
import com.sns.room.follow.dto.FollowingResponseDto;
import com.sns.room.follow.entity.Follow;
import com.sns.room.follow.repository.FollowRepository;
import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import com.sns.room.user.entity.User;
import com.sns.room.user.service.AuthService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final AuthService authService;
    private final PostService postService;

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

    public List<FollowingResponseDto> getFollowingList(User fromUser) {
        List<Follow> follows = followRepository.findAllByFromUserId(fromUser.getId());

        return follows.stream()
            .map(FollowingResponseDto::new)
            .collect(Collectors.toList());
    }

    public List<FollowerResponseDto> getFollowerList(User toUser) {
        List<Follow> follows = followRepository.findAllByToUserId(toUser.getId());

        return follows.stream()
            .map(FollowerResponseDto::new)
            .collect(Collectors.toList());
    }

    public List<PostResponseDto> getAllFollowingPost(User fromUser) {
        List<Follow> follows = followRepository.findAllByFromUserId(fromUser.getId());
        List<Post> posts = new ArrayList<>();

        for (Follow follow : follows) {
            Long toUserId = follow.getToUserId();
            posts.addAll(postService.findByUserId(toUserId));
        }
        return posts.stream()
            .map(PostResponseDto::new)
            .toList();
    }

    public Follow findLatestUser(Long toUserId) {
        return followRepository.findFirstByToUserIdOrderByCreatedAtDesc(toUserId)
            .orElseThrow(() -> new IllegalArgumentException("팔로우를 찾을 수 없습니다."));
    }
}
