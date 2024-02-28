package com.sns.room.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.user.dto.PasswordUpdateRequestDto;
import com.sns.room.user.dto.UserRequestDto;
import com.sns.room.user.dto.UserResponseDto;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetailsImpl;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("TestPassword");
        user.setIntroduce("Test introduction");
        user.setRole(UserRoleEnum.USER);

        this.userDetailsImpl = new UserDetailsImpl(user);

        this.userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("newTestUser");
        userRequestDto.setIntroduce("New Test introduction");

        this.userResponseDto = new UserResponseDto(user);
    }

    @Test
    @WithMockUser
    void getUserProfile_Success() throws Exception {

        given(authService.getUserProfile(userDetailsImpl.getUser().getId())).willReturn(userResponseDto);

        mockMvc.perform(get("/api/auth/mypage").with(user(userDetailsImpl)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.username").value("testUser"))
            .andExpect(jsonPath("$.data.introduce").value("Test introduction"));
    }

    @Test
    void updateUser_Success() throws Exception {

        given(authService.updateUser(any(UserDetailsImpl.class), any(UserRequestDto.class))).willReturn(userResponseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String userRequestDtoJson = objectMapper.writeValueAsString(userRequestDto);

        mockMvc.perform(put("/api/auth/mypage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestDtoJson)
                .with(user(userDetailsImpl)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("프로필 수정 성공"))
            .andExpect(jsonPath("$.data.username").value("testUser"))
            .andExpect(jsonPath("$.data.introduce").value("Test introduction"));
    }

    @Test
    @WithMockUser
    void updatePassword_Success() throws Exception {
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setPassword("currentPassword");
        passwordUpdateRequestDto.setChangePassword("newPassword");
        passwordUpdateRequestDto.setChangePasswordCheck("newPassword");

        // 비밀번호 변경 작업이 성공적으로 수행되었을 때 아무것도 반환하지 않음
        doNothing().when(authService).updatePassword(any(UserDetailsImpl.class), any(PasswordUpdateRequestDto.class));

        User testUser = new User("testUser", "testPassword", "Test introduction", UserRoleEnum.USER);
        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);

        mockMvc.perform(put("/api/auth/password-patch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateRequestDto))
                .with(user(userDetails.getUsername())))
            .andExpect(status().isOk());
    }

}
