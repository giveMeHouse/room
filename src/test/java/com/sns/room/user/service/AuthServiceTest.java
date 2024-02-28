package com.sns.room.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sns.room.global.jwt.JwtUtil;
import com.sns.room.user.dto.LoginRequestDto;
import com.sns.room.user.dto.SignupRequestDto;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)

class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Mock
	private HttpServletResponse response;

	@Test
	@DisplayName("회원가입")
	void signup() {
		// given
		SignupRequestDto dto = new SignupRequestDto();
		dto.setUsername("testUser");
		dto.setEmail("test@email.com");
		dto.setPassword("testPassword");
		dto.setRole(UserRoleEnum.USER);

		given(userRepository.findByUsername(dto.getUsername())).willReturn(Optional.empty());
		given(userRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());
		given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPassword");

		// when
		authService.signup(dto);

		// then
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 실패 - username")
	void signupUsername() {
		// given
		SignupRequestDto dto = new SignupRequestDto();
		dto.setUsername("testUser");

		when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new User()));

		// when
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.signup(dto);
		});

		// then
		assertEquals("중복된 username입니다.", exception.getMessage());
	}

	@Test
	@DisplayName("회원가입 실패 - email")
	void signupEmail() {
		// given
		SignupRequestDto dto = new SignupRequestDto();
		dto.setEmail("test@emai.com");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

		// when
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.signup(dto);
		});

		// then
		assertEquals("중복된 email입니다.", exception.getMessage());
	}


	@Test
	@DisplayName("로그인")
	void loginTest() {
		// given
		LoginRequestDto dto = new LoginRequestDto();
		dto.setUsername("testUser");
		dto.setPassword("testPassword");

		User user = new User();
		ReflectionTestUtils.setField(user, "username", "testUser");
		ReflectionTestUtils.setField(user, "password","encodePassword");
		ReflectionTestUtils.setField(user, "role", UserRoleEnum.USER);

		given(userRepository.findByUsername("testUser")).willReturn(java.util.Optional.of(user));
		given(passwordEncoder.matches("testPassword", "encodePassword")).willReturn(true);
		given(jwtUtil.createToken("testUser", UserRoleEnum.USER)).willReturn("Token");

		// when
		authService.login(dto, response);

		// then
		verify(userRepository, times(1)).findByUsername("testUser");
		verify(passwordEncoder, times(1)).matches("testPassword", "encodePassword");
	}


	@Test
	@DisplayName("로그인 실패 - username")
	void loginUsername() {
		// given
		LoginRequestDto loginRequestDto = new LoginRequestDto("testUsername", "testPassword");

		when(userRepository.findByUsername(loginRequestDto.getUsername())).thenReturn(Optional.empty());

		// when
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(loginRequestDto, response);
		});

		// then
		assertEquals("존재하지 않는 username입니다.", exception.getMessage());
	}


	@Test
	@DisplayName("로그인 실패 - password")
	void loginPassword() {
		// given
		String username = "testUsername";
		String password = "5000";
		LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

		User user = new User();
		ReflectionTestUtils.setField(user, "username", "testUsername");
		ReflectionTestUtils.setField(user, "password", "5050");

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

		// when, then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(loginRequestDto, response);
		});

		assertEquals("잘못된 비밀번호 입니다.", exception.getMessage());
	}

}
