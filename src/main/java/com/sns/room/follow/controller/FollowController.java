package com.sns.room.follow.controller;

import com.sns.room.follow.dto.FollowerResponseDto;
import com.sns.room.follow.dto.FollowingResponseDto;
import com.sns.room.follow.service.FollowService;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.notification.service.NotificationService;
import com.sns.room.post.dto.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Follow", description = "팔로우 컨트롤러")
public class FollowController {

    private final FollowService followService;
    private final NotificationService notificationService;

    @Operation(summary = "팔로우 하기", description = "팔로우 할 수 있는 API")
    @PostMapping("/follows/{toUserId}")
    public ResponseEntity<Void> createFollow(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long toUserId) {
        followService.createFollow(userDetails.getUser(), toUserId);
        notificationService.notifyFollow(toUserId);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    @Operation(summary = "팔로우 취소", description = "팔로우 취소할 수 있는 API")
    @DeleteMapping("/follows/{toUserId}")
    public ResponseEntity<Void> deleteFollow(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long toUserId) {
        followService.deleteFollow(userDetails.getUser(), toUserId);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    @Operation(summary = "팔로잉 목록 조회", description = "유저의 팔로잉 목록을 조회할 수 있는 API")
    @GetMapping("/users/{userId}/follows/following")
    public List<FollowingResponseDto> getFollowingList(
        @PathVariable Long userId) {
        List<FollowingResponseDto> followingResponseDtos =
            followService.getFollowingList(userId);

        return ResponseEntity.status(HttpStatus.OK.value()).body(followingResponseDtos).getBody();
    }

    @Operation(summary = "팔로워 목록 조회", description = "유저의 팔로워 목록을 조회할 수 있는 API")
    @GetMapping("/users/{userId}/follows/follower")
    public List<FollowerResponseDto> getFollowerList(
        @PathVariable Long userId) {
        List<FollowerResponseDto> followerResponseDtos =
            followService.getFollowerList(userId);

        return ResponseEntity.status(HttpStatus.OK.value()).body(followerResponseDtos).getBody();
    }

    @Operation(summary = "팔로잉 게시글 목록 조회", description = "팔로잉한 유저의 전체 게시글 목록을 조회할 수 있는 API")
    @GetMapping("/follows/post")
    public ResponseEntity<List<PostResponseDto>> getAllFollowingPost(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PostResponseDto> postResponseDtos =
            followService.getAllFollowingPost(userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(postResponseDtos);
    }
}
