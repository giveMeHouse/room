package com.sns.room.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserResponse<T> {
	private T data;
	private String message;
}
