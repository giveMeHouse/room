package com.sns.room.user.service;



import com.sns.room.global.jwt.JwtUtil;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.user.dto.PasswordUpdateRequestDto;
import com.sns.room.user.dto.UserRequestDto;
import com.sns.room.user.dto.UserResponseDto;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void 프로필_조회_성공() {
        // given
        User user = new User();
        user.setUsername("testUser");
        user.setIntroduce("Test introduction");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponseDto result = authService.getUserProfile(1L);

        // then
        assertEquals("testUser", result.getUsername());
        assertEquals("Test introduction", result.getIntroduce());
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
        // given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("updatedUser");
        userRequestDto.setIntroduce("Updated introduction");

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setIntroduce("Test introduction");

        UserDetailsImpl mockUserDetailsImpl = mock(UserDetailsImpl.class);
        when(mockUserDetailsImpl.getUser()).thenReturn(user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponseDto result = authService.updateUser(mockUserDetailsImpl, userRequestDto);

        // then
        assertEquals("updatedUser", result.getUsername());
        assertEquals("Updated introduction", result.getIntroduce());
    }

    @Test
    void 비밀번호_변경_성공() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldEncodedPassword");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        JwtUtil mockJwtUtil = mock(JwtUtil.class); // JwtUtil 모의 객체 생성

        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mockPasswordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AuthService authService = new AuthService(mockUserRepository, mockPasswordEncoder, mockJwtUtil);

        PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("oldPassword", "newPassword", "newPassword");

        // When
        authService.updatePassword(userDetails, requestDto);

        // Then
        verify(userRepository).save(any(User.class));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());
        assertEquals("encodedNewPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void 비밀번호_변경_실패_이전_비밀번호_불일치() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setPassword("oldEncodedPassword");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        JwtUtil mockJwtUtil = mock(JwtUtil.class); // JwtUtil 모의 객체 생성

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(false); // 이전 비밀번호 불일치 시뮬레이션
        AuthService authService = new AuthService(mockUserRepository, mockPasswordEncoder, mockJwtUtil);

        PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("wrongOldPassword", "newPassword", "newPassword");

        // When
        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> {
            authService.updatePassword(userDetails, requestDto);
        });

        // Then
        assertEquals("이전 비밀번호가 일치하지 않습니다.", thrown.getMessage());
        verify(mockUserRepository, never()).save(any(User.class));
    }

    @Test
    void 비밀번호_변경_실패_기존_비밀번호와_동일() {
        // Given
        User user = new User();
        user.setId(1L);
        // 가정된 기존 암호화된 비밀번호
        user.setPassword("encodedOldPassword");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
        UserRepository mockUserRepository = mock(UserRepository.class);
        JwtUtil mockJwtUtil = mock(JwtUtil.class); // JwtUtil 모의 객체 생성

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(mockPasswordEncoder.encode(anyString())).thenReturn(user.getPassword());

        AuthService authService = new AuthService(mockUserRepository, mockPasswordEncoder, mockJwtUtil);

        // 기존 비밀번호와 동일한 새 비밀번호로 요청 생성
        PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("oldPassword", "oldEncodedPassword", "oldEncodedPassword");

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.updatePassword(userDetails, requestDto);
        });
    }


}
