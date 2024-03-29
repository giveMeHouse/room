package com.sns.room.user.repository;

import com.sns.room.post.entity.Post;
import com.sns.room.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
}
