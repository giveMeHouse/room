package com.sns.room.post.dto;

import com.sns.room.post.entity.Post;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private String title;
    private String content;
    private String username;
    private String photo;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.photo = post.getPhoto();
        this.createdAt = post.getCreateAt();
        this.username = post.getUser().getUsername();
        this.modifiedAt = post.getModifiedAt();
    }
}
