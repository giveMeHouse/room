package com.sns.room.user.controller;

import com.sns.room.user.CommonResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<String>> signup(
			@RequestBody SignupRequestDto signupRequestDto,
			BindingResult bindingResult) {

			if (bindingResult.hasErrors()) {
				List<String> errorMessages = new ArrayList<>();
				for (FieldError error : bindingResult.getFieldErrors()) {
					errorMessages.add(error.getDefaultMessage());
				}
				return ResponseEntity.badRequest().body(
						CommonResponse.<String>builder()
								.data(null).message("회원가입 실패 :" + errorMessages).build()
				);
			}

		authService.signup(signupRequestDto);

			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(
				CommonResponse.<String>builder().data(
						signupRequestDto.getUsername()
				).message("회원가입 성공").build()
		);
	}

	@GetMapping("/login")
	public ResponseEntity<CommonResponse<String>> login(
			@RequestBody LoginRequestDto loginRequestDto,
			HttpServletResponse res) {

		authService.login(loginRequestDto, res);

		return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(
				CommonResponse.<String>builder().data(
						loginRequestDto.getUsername()
				).message("로그인 성공").build()
		);
	}

}
