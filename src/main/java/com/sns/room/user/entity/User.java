package com.sns.room.user.entity;

import com.sns.room.global.util.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor

public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column
    private String introduce;

    @Builder
    public User(String username, String email, String password, UserRoleEnum role,
        String introduce) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.introduce = introduce;
    }

    public User(String username, String email, String password, UserRoleEnum role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void update(String username, String introduce) {
        this.username = username;
        this.introduce = introduce;
    }

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }
}
