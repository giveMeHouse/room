package com.sns.room.like.controller;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.like.dto.LikeResponseDto;
import com.sns.room.like.service.LikeService;
import com.sns.room.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Like", description = "좋아요 컨트롤러")
@RequestMapping("/posts/{postId}/like")
public class LikeController {

    private final LikeService likeService;
    private final NotificationService notificationService;

    @Operation(summary = "좋아요 생성", description = "좋아요 생성하는 API")
    @PostMapping
    public ResponseEntity<Void> createLike(@PathVariable Long postId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.createLike(postId, userDetails.getUser());
        notificationService.notifyLike(postId);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    @Operation(summary = "좋아요 수 조회", description = "좋아요의 수를 조회하는 API")
    @GetMapping
    public ResponseEntity<LikeResponseDto> countLikes(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(
            LikeResponseDto.builder().likeCount(likeService.countLikes(postId)).build());
    }

    @Operation(summary = "좋아요 취소", description = "좋아요 취소하는 API")
    @DeleteMapping
    public ResponseEntity<Void> deleteLike(@PathVariable Long postId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deleteLike(postId, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }
}
