package com.sns.room.comment.dto;

import lombok.Getter;

@Getter
public class CommentResponseDto {

    private String postTitle;
    private String username;
    private String comment;

    public CommentResponseDto(String postTitle, String username, String comment) {
        this.postTitle = postTitle;
        this.username = username;
        this.comment = comment;
    }
}
