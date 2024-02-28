package com.sns.room.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponseDto {

    private Long likeCount;
}
