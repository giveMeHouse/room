package com.sns.room.follow.dto;

import com.sns.room.follow.entity.Follow;
import lombok.Getter;

@Getter
public class FollowerResponseDto {

    private Long fromUserId;
    private String username;

    public FollowerResponseDto(Follow follow, String username) {
        this.fromUserId = follow.getFromUserId();
        this.username = username;
    }
}
