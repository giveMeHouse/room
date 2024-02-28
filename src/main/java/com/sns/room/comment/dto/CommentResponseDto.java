package com.sns.room.comment.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private String postTitle;
    private String username;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(String postTitle, String username, String comment,
        LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.postTitle = postTitle;
        this.username = username;
        this.comment = comment;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
