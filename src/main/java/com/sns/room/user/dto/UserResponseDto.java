package com.sns.room.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserResponseDto<T> {
	private T data;
	private String message;
}
