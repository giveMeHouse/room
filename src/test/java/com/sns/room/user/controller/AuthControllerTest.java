package com.sns.room.user.controller;

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
}
