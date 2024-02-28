package com.sns.room.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
@SpringBootTest
@ExtendWith(MockitoExtension.class)

class AuthServiceTest {

	@Autowired
	private AuthService authService;
	@MockBean
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입")
	void signup() {
		SignupRequestDto dto = new SignupRequestDto();
		ReflectionTestUtils.setField(dto, "username", "testUser");
		ReflectionTestUtils.setField(dto, "email", "test@example.com");
		ReflectionTestUtils.setField(dto, "password","testPassword");
		ReflectionTestUtils.setField(dto, "role", UserRoleEnum.USER);

		User user = new User(dto.getUsername(),dto.getEmail(),dto.getPassword(),dto.getRole());

		when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(user.getPassword())).thenReturn("password");

		userRepository.save(user);
		authService.signup(dto);

		assertEquals(1,userRepository.count());
	}

	@Test
	void loginUsername() {
		LoginRequestDto loginRequestDto = new LoginRequestDto("testUsername", "testPassword");
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(userRepository.findByUsername(loginRequestDto.getUsername())).thenReturn(Optional.empty());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(loginRequestDto, response);
		});

		assertEquals("존재하지 않는 username입니다.", exception.getMessage());
	}

	@Test
	void loginPassword() {
		// Given
		String username = "testUsername";
		String password = "5000";
		LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);

		HttpServletResponse response = mock(HttpServletResponse.class);

		User user = new User();
		ReflectionTestUtils.setField(user, "username", "testUsername");
		ReflectionTestUtils.setField(user, "password", "5050");


		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

		// When, Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(loginRequestDto, response);
		});

		assertEquals("잘못된 비밀번호 입니다.", exception.getMessage());
	}

}
