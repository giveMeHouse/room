package com.sns.room.comment.repository;

import com.sns.room.comment.entity.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findFirstByPostIdOrderByCreatedAtDesc(Long id);
}
