package com.sns.room.user.dto;

import com.sns.room.user.entity.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {
	@NotBlank
	@Pattern(regexp = "^[a-zA-z0-9]{4,10}$", message = "최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)")
	private String username;
	private String email;
	@Pattern(regexp = "^[a-zA-z0-9!@#$%^&*()]{8,15}$", message = "최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자")
	private String password;
	private UserRoleEnum role;
	private String adminToken ="";
}
