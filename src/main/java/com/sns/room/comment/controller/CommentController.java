package com.sns.room.comment.controller;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.dto.ResponseDto;
import com.sns.room.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<ResponseDto> createComment(@PathVariable Long postId,
        @RequestBody CommentRequestDto commentRequestDto) {
        long userId = 1L;
        CommentResponseDto responseDto = commentService.createComment(commentRequestDto, postId,
            userId);
        return ResponseEntity.ok().body(new ResponseDto("댓글 생성 성공", responseDto));
    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long postId,
        @PathVariable Long commentId,
        @RequestBody CommentRequestDto commentRequestDto) {
        long userId = 1L;
        CommentResponseDto responseDto = commentService.updateComment(commentRequestDto, postId,
            userId, commentId);
        return ResponseEntity.ok().body(new ResponseDto("댓글 수정 성공", responseDto));
    }
}
