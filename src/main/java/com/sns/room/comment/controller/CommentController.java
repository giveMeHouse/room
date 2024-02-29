package com.sns.room.comment.controller;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.dto.ResponseDto;
import com.sns.room.comment.service.CommentService;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ResponseDto> createComment(@PathVariable Long postId,
        @RequestBody CommentRequestDto commentRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.createComment(commentRequestDto, postId,
            userDetails.getUser().getId());
        notificationService.notifyComment(postId);
        return ResponseEntity.ok().body(new ResponseDto(responseDto));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long postId,
        @PathVariable Long commentId,
        @RequestBody CommentRequestDto commentRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.updateComment(commentRequestDto, postId,
            userDetails.getUser().getId(), commentId);
        return ResponseEntity.ok().body(new ResponseDto(responseDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long postId,
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.deleteComment(postId,
            userDetails.getUser().getId(), commentId);
        return ResponseEntity.ok().body(new ResponseDto(responseDto));
    }

    @GetMapping
    public List<CommentResponseDto> getAllComment() {
        return commentService.getAllComment();
    }
}
