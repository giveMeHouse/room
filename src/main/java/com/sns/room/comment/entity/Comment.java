package com.sns.room.comment.entity;

import com.sns.room.post.entity.Post;
import com.sns.room.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@SQLRestriction("deleted_at is NULL")
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(nullable = false)
    private String comment;

    @Column
    private LocalDateTime deleted_at = null; // 기본값을 null로 설정

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    public Comment(String comment, Post post, User user) {
        this.comment = comment;
        this.user = user;
        this.post = post;
    }

    public void Update(String comment) {
        this.comment = comment;
    }

    public void SoftDeleted() {
        this.deleted_at = LocalDateTime.now();
    }
}
