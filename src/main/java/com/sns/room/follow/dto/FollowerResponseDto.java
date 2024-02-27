package com.sns.room.follow.dto;

import com.sns.room.follow.entity.Follow;
import lombok.Getter;

@Getter
public class FollowerResponseDto {

    private Long fromUserId;

    public FollowerResponseDto(Follow follow) {
        this.fromUserId = follow.getFromUserId();
    }
}
