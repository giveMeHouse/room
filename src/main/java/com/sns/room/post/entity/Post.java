package com.sns.room.post.entity;

import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "posts")

public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String photo;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createAt;
    @Column
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Post(PostRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.user = user;
        this.photo = requestDto.getPhoto();
        this.createAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
//    public Post(String title,String content,String photo,LocalDateTime createAt,LocalDateTime modifiedAt,User user){
//        this.title = title;
//        this.content = content;
//        this.user = user;
//        this.photo = photo;
//        this.createAt = LocalDateTime.now();
//        this.modifiedAt = LocalDateTime.now();
//    }
    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
    

}
