package com.sns.room.user.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sns.room.global.jwt.JwtUtil;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.user.dto.LoginRequestDto;
import com.sns.room.user.dto.PasswordUpdateRequestDto;
import com.sns.room.user.dto.SignupRequestDto;
import com.sns.room.user.dto.UserRequestDto;
import com.sns.room.user.dto.UserResponseDto;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthServiceTest {

	@Autowired
	private AuthService authService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@Mock
	private HttpServletResponse response;

	@Mock
	private UserDetailsImpl userDetails;

	@Mock
	private JwtUtil jwtUtil;

	private User user;

	@BeforeEach
	void setUp() {
		// User 객체 생성 시 생성자를 사용하거나 Builder 패턴을 적용
		user = new User("testUser", "TestEmail", "Test password", UserRoleEnum.USER,
			"Test introduction");
		when(userDetails.getUser()).thenReturn(user);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
	}

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
		ReflectionTestUtils.setField(user, "password", "encodePassword");
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

		when(userRepository.findByUsername(loginRequestDto.getUsername())).thenReturn(
			Optional.empty());

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

		@Test
		void 프로필_조회_성공() {
			when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
			UserResponseDto userResponseDto = authService.getUserProfile(user.getId());

			assertThat(userResponseDto.getUsername()).isEqualTo(user.getUsername());
			assertThat(userResponseDto.getIntroduce()).isEqualTo(user.getIntroduce());
		}

		@Test
		void 유저를_찾을_수_없을_때() {
			// given
			given(userRepository.findById(anyLong())).willReturn(Optional.empty());

			// when & then
			assertThrows(IllegalArgumentException.class, () -> {
				authService.getUserProfile(1L);
			});
		}

		@Test
		void 프로필_수정_성공() {
			UserRequestDto updateDto = new UserRequestDto();
			updateDto.setUsername("updatedUsername");
			updateDto.setIntroduce("Updated introduction");

			when(userRepository.save(any(User.class))).thenReturn(user);

			UserResponseDto updatedUser = authService.updateUser(userDetails, updateDto);

			assertThat(updatedUser.getUsername()).isEqualTo(updateDto.getUsername());
			assertThat(updatedUser.getIntroduce()).isEqualTo(updateDto.getIntroduce());
		}

		@Test
		void 비밀번호_변경_성공() {
			PasswordUpdateRequestDto passwordUpdateDto = new PasswordUpdateRequestDto("oldPassword",
				"newPassword", "newPassword");

			when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);

			when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

			authService.updatePassword(userDetails, passwordUpdateDto);

			verify(userRepository, times(1)).save(user);
			verify(passwordEncoder, times(1)).encode("newPassword");
		}

		@Test
		void 비밀번호_변경_실패_이전_비밀번호_불일치() {
			// Given
			User user = new User("testUser", "testEmail", "oldEncodedPassword", UserRoleEnum.USER,
				"Test introduction");
			UserDetailsImpl userDetails = new UserDetailsImpl(user);

			PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
			UserRepository mockUserRepository = mock(UserRepository.class);
			JwtUtil mockJwtUtil = mock(JwtUtil.class); // JwtUtil 모의 객체 생성

			when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
			when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(
				false); // 이전 비밀번호 불일치 시뮬레이션

			AuthService authService = new AuthService(mockUserRepository, mockPasswordEncoder,
				mockJwtUtil);
			PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("wrongOldPassword",
				"newPassword", "newPassword");

			// When
			BadCredentialsException thrown = assertThrows(BadCredentialsException.class,
				() -> authService.updatePassword(userDetails, requestDto));

			// Then
			assertEquals("이전 비밀번호가 일치하지 않습니다.", thrown.getMessage());
			verify(mockUserRepository, never()).save(any(User.class));
		}

		@Test
		void 비밀번호_변경_실패_기존_비밀번호와_동일() {
			// Given
			User user = new User("testUser", "testEmail", "encodedOldPassword", UserRoleEnum.USER,
				"Test introduction");
			UserDetailsImpl userDetails = new UserDetailsImpl(user);

			PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
			UserRepository mockUserRepository = mock(UserRepository.class);
			JwtUtil mockJwtUtil = mock(JwtUtil.class); // JwtUtil 모의 객체 생성

			when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
			when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
			when(mockPasswordEncoder.encode(anyString())).thenReturn(user.getPassword());

			AuthService authService = new AuthService(mockUserRepository, mockPasswordEncoder,
				mockJwtUtil);

			// 기존 비밀번호와 동일한 새 비밀번호로 요청 생성
			PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("oldPassword",
				"oldEncodedPassword", "oldEncodedPassword");

			// When & Then
			assertThrows(BadCredentialsException.class,
				() -> authService.updatePassword(userDetails, requestDto));
		}

	}
