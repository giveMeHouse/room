package com.sns.room.user.service;


import com.sns.room.global.jwt.JwtUtil;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.user.dto.PasswordUpdateRequestDto;
import com.sns.room.user.dto.UserRequestDto;
import com.sns.room.user.dto.UserResponseDto;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsImpl userDetails;

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
        PasswordUpdateRequestDto passwordUpdateDto = new PasswordUpdateRequestDto("oldPassword", "newPassword", "newPassword");

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
