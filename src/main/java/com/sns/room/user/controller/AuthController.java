package com.sns.room.user.controller;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.user.dto.ResponseDto;
import com.sns.room.user.dto.LoginRequestDto;
import com.sns.room.user.dto.SignupRequestDto;
import com.sns.room.user.dto.UserRequestDto;
import com.sns.room.user.dto.UserResponseDto;
import com.sns.room.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
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

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(
            ResponseDto.<String>builder().data(
                signupRequestDto.getUsername()
            ).message("회원가입 성공").build()
        );
    }

    @GetMapping("/login")
    public ResponseEntity<ResponseDto<String>> login(
        @RequestBody LoginRequestDto loginRequestDto,
        HttpServletResponse res) {

        authService.login(loginRequestDto, res);

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(
            ResponseDto.<String>builder().data(
                loginRequestDto.getUsername()
            ).message("로그인 성공").build()
        );
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserResponseDto userResponseDto = AuthService.getUser(userDetails);
        return ResponseEntity.ok()
            .body(ResponseDto.<UserResponseDto>builder()
                .message("프로필 조회 성공")
                .data(userResponseDto)
                .build());
    }

    @PutMapping("/mypage")
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UserRequestDto userRequestDto) {

        UserResponseDto updatedUser = authService.updateUser(userDetails, userRequestDto);
        return ResponseEntity.ok()
            .body(ResponseDto.<UserResponseDto>builder()
                .message("프로필 수정 성공")
                .data(updatedUser)
                .build());
    }



}
