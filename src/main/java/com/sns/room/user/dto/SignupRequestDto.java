package com.sns.room.user.dto;

import com.sns.room.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    private String username;
    private String email;
    private String password;
    private UserRoleEnum role;
    private String adminToken = "";
}
