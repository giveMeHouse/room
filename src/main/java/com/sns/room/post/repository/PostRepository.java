package com.sns.room.post.repository;

import com.sns.room.post.entity.Post;
import com.sns.room.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long id);
}
