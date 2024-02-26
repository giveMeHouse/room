package com.sns.room.follow.controller;

import com.sns.room.follow.dto.FollowResponseDto;
import com.sns.room.follow.service.FollowService;
import com.sns.room.global.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follows/{toUserId}")
    public ResponseEntity<FollowResponseDto> createFollow(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long toUserId) {
        followService.createFollow(userDetails.getUser(), toUserId);
        return ResponseEntity.status(HttpStatus.OK.value())
                .body(FollowResponseDto.builder().message("팔로우 성공하였습니다.").build());
    }
}
