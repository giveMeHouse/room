package com.sns.room.user.controller;

import com.sns.room.user.dto.ResponseDto;
import com.sns.room.user.dto.LoginRequestDto;
import com.sns.room.user.dto.SignupRequestDto;
import com.sns.room.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
public class AuthController {

	private final AuthService authService;
	@PostMapping("/signup")
	public ResponseEntity<ResponseDto<String>> signup(
			@RequestBody SignupRequestDto signupRequestDto,
			BindingResult bindingResult) {

			if (bindingResult.hasErrors()) {
				List<String> errorMessages = new ArrayList<>();
				for (FieldError error : bindingResult.getFieldErrors()) {
					errorMessages.add(error.getDefaultMessage());
				}
				return ResponseEntity.badRequest().body(
						ResponseDto.<String>builder()
								.data(null).message("회원가입 실패 :" + errorMessages).build()
				);
			}

		authService.signup(signupRequestDto);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
	}


	@PostMapping("/login")
	public ResponseEntity<ResponseDto<String>> login(
			@RequestBody LoginRequestDto loginRequestDto,
			HttpServletResponse res) {

		authService.login(loginRequestDto, res);

		return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
	}

}
