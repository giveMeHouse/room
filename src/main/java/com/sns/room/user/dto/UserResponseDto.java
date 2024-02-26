package com.sns.room.user.dto;


import com.sns.room.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String introduce;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.introduce = user.getIntroduce();
    }

}
