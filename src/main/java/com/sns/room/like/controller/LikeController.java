package com.sns.room.like.controller;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.like.dto.LikeResponseDto;
import com.sns.room.like.service.LikeService;
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
@RequestMapping("/posts/{postId}/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponseDto> createLike(@PathVariable Long postId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.createLike(postId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK.value())
            .body(LikeResponseDto.builder().message("좋아요 생성").build());
    }

    @GetMapping
    public ResponseEntity<LikeResponseDto> countLikes(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(
            LikeResponseDto.builder().likeCount(likeService.countLikes(postId)).message("좋아요 수 조회")
                .build());
    }

    @DeleteMapping
    public ResponseEntity<LikeResponseDto> deleteLike(@PathVariable Long postId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        likeService.deleteLike(postId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK.value())
            .body(LikeResponseDto.builder().message("좋아요 취소").build());
    }
}