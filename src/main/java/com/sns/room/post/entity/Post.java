package com.sns.room.post.entity;

import com.sns.room.global.util.Timestamped;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
<<<<<<< HEAD
import java.time.LocalDateTime;
=======
import lombok.AllArgsConstructor;
import lombok.Builder;
>>>>>>> e9ea86afe974134c03a29b83391309047e619882
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")

public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column
    private String photo;
<<<<<<< HEAD
    @Column
    private LocalDateTime create_at;
=======

>>>>>>> e9ea86afe974134c03a29b83391309047e619882
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post(PostRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.user = user;
        this.photo = requestDto.getPhoto();
        this.create_at = LocalDateTime.now();
    }

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
