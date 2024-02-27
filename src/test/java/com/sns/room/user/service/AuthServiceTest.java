//package com.sns.room.user.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.sns.room.user.dto.LoginRequestDto;
//import com.sns.room.user.dto.SignupRequestDto;
//import com.sns.room.user.entity.User;
//import com.sns.room.user.entity.UserRoleEnum;
//import com.sns.room.user.repository.UserRepository;
//import jakarta.servlet.http.HttpServletResponse;
//import java.util.Optional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//	@Mock
//	private AuthService authService;
//	@Mock
//	private UserRepository userRepository;
//	@Mock
//	private PasswordEncoder passwordEncoder;
//	@Captor
//	private ArgumentCaptor<User> userCaptor;
//
//	@Test
//	void signup() {
//
//	}
//
//	@Test
//	void login() {
//	}
//
//}
