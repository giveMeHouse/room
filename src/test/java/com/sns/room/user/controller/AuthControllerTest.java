package com.sns.room.user.controller;

<<<<<<< HEAD
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
        User user = User.builder()
            .username("testUser")
            .password("TestPassword")
            .introduce("Test introduction")
            .role(UserRoleEnum.USER)
            .build();

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
            .andExpect(jsonPath("$.data.username").value("testUser"))
            .andExpect(jsonPath("$.data.introduce").value("Test introduction"));
    }

    @Test
    @WithMockUser
    void updatePassword_Success() throws Exception {
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto("currentPassword", "newPassword", "newPassword");

        doNothing().when(authService).updatePassword(any(UserDetailsImpl.class), any(PasswordUpdateRequestDto.class));

        mockMvc.perform(put("/api/auth/password-patch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateRequestDto))
                .with(user(userDetailsImpl.getUsername())))
            .andExpect(status().isOk());
    }

=======
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sns.room.user.dto.LoginRequestDto;
import com.sns.room.user.dto.ResponseDto;
import com.sns.room.user.dto.SignupRequestDto;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

class AuthControllerTest {

	@Mock
	AuthService authService;
	@InjectMocks
	private AuthController authController;
	@BeforeEach
	void setUp() {
		authService = mock(AuthService.class);
		authController = new AuthController(authService);
	}

	@Test
	void signup() {
		SignupRequestDto dto = new SignupRequestDto();
		dto.setUsername("testuser");
		dto.setEmail("test@email.coom");
		dto.setPassword("testpassword");
		dto.setRole(UserRoleEnum.USER);

		BindingResult bindingResult = mock(BindingResult.class);

		ResponseEntity<ResponseDto<String>> responseEntity = authController.signup(dto, bindingResult);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	void falseSignup() {
		SignupRequestDto dto = new SignupRequestDto();
		dto.setUsername("testuser");
		dto.setEmail("test@email.coom");
		dto.setPassword("testpassword");
		dto.setRole(UserRoleEnum.USER);

		BindingResult bindingResult = mock(BindingResult.class);
		given(bindingResult.hasErrors()).willReturn(true);

		List<FieldError> fieldErrors = new ArrayList<>();
		fieldErrors.add(new FieldError("usernmae", "errorCode", "errorMessages"));
		fieldErrors.add(new FieldError("email", "errorCode", "errorMessages"));
		given(bindingResult.getFieldErrors()).willReturn(fieldErrors);

		// when
		ResponseEntity<ResponseDto<String>> responseEntity = authController.signup(dto, bindingResult);

		// then
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}


	@Test
	void login() {
		LoginRequestDto dto = new LoginRequestDto();
		dto.setUsername("testuser");
		dto.setPassword("testpassword");

		HttpServletResponse res = mock(HttpServletResponse.class);

		// when
		ResponseEntity<ResponseDto<String>> responseEntity = authController.login(dto, res);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
>>>>>>> a740edee833278ea174d42b5e2bc4b626bb78829
}
