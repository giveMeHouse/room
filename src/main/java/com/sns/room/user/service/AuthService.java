package com.sns.room.user.service;

import com.sns.room.global.exception.InvalidInputException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZaHgTBcXukeZygoC";

    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 username입니다..");
        }

        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 email입니다.");
        }

        UserRoleEnum role = signupRequestDto.getRole();
        if (role.equals(UserRoleEnum.ADMIN)) {
            if (!ADMIN_TOKEN.equals(signupRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 일치하지 않습니다.");
            }
        }

        User user = new User(username, email, password, role);
        userRepository.save(user);
    }

    public void login(LoginRequestDto loginRequestDto, HttpServletResponse res) {

        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 username입니다.")
        );
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        jwtUtil.addJwtToHeader(token, res);
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new InvalidInputException("해당 User는 존재하지 않습니다."));
    }


    public UserResponseDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
        UserRequestDto userRequestDto) {
        // 토큰으로 id 가져오기
        Long userId = userDetails.getUser().getId();
        // DB에 접근
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
        // 변경
        user.update(userRequestDto.getUsername(), userRequestDto.getIntroduce());
        userRepository.save(user);
        return new UserResponseDto(user);
    }

    @Transactional
    public void updatePassword(UserDetailsImpl userDetails,
        PasswordUpdateRequestDto passwordUpdateRequestDto) {
        // 토큰으로 id 가져오기
        Long userId = userDetails.getUser().getId();
        System.out.println("Looking for user ID: " + userId);
        // DB에 접근
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
        System.out.println("User found: " + user.getId());

        // 기존 비밀번호 확인
        validatePassword(passwordUpdateRequestDto.getPassword(), user.getPassword());

        // 새 비밀번호와 새 비밀번호 확인 값 비교
        validateNewPassword(passwordUpdateRequestDto.getChangePassword(), user.getPassword(),
            passwordUpdateRequestDto.getChangePasswordCheck());

        // 변경
        user.updatePassword(passwordUpdateRequestDto.getChangePassword(), passwordEncoder);
        userRepository.save(user);
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("이전 비밀번호가 일치하지 않습니다.");
        }
    }

    private void validateNewPassword(String newPassword, String oldEncodedPassword,
        String checkPassword) {
        if (!newPassword.equals(checkPassword)) {
            throw new BadCredentialsException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(newPassword, oldEncodedPassword)) {
            throw new BadCredentialsException("새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다.");
        }
    }



}

