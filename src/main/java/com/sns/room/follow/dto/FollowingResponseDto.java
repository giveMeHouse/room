package com.sns.room.follow.dto;

import com.sns.room.follow.entity.Follow;
import lombok.Getter;

@Getter
public class FollowingResponseDto {

    private Long toUserId;

    public FollowingResponseDto(Follow follow) {
        this.toUserId = follow.getToUserId();
    }
}
