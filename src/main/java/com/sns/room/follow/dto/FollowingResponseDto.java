package com.sns.room.follow.dto;

import com.sns.room.follow.entity.Follow;
import lombok.Getter;

@Getter
public class FollowingResponseDto {

    private Long toUserId;
    private String username;

    public FollowingResponseDto(Follow follow, String username) {
        this.toUserId = follow.getToUserId();
        this.username = username;
    }
}
