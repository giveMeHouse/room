package com.sns.room.post.dto;

import com.sns.room.post.entity.Post;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String username;
    private String photo;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.photo = post.getPhoto();
        this.createdAt = post.getCreatedAt();
        this.username = post.getUser().getUsername();
        this.modifiedAt = post.getModifiedAt();
    }
}
