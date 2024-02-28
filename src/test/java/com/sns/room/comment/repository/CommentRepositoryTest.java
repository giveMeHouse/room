package com.sns.room.comment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sns.room.comment.entity.Comment;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.entity.Post;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;


    @Test
    @Disabled
    @DisplayName("댓글 저장 테스트")
    void save() {
        User user = new User("username", "email", "password", UserRoleEnum.USER);
        Post post = new Post(new PostRequestDto("title", "content", "photo", LocalDateTime.now()),
            user);

        Comment comment = new Comment("comment", post, user);

        commentRepository.save(comment);

        Comment savedComment = commentRepository.findById(comment.getCommentId()).orElseThrow(null);

        assertEquals(comment.getCommentId(), savedComment.getCommentId());
    }

}
