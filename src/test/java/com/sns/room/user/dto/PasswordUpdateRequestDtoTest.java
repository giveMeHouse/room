package com.sns.room.user.dto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordUpdateRequestDtoTest {

    @Test
    @DisplayName("PasswordUpdateRequestDto 생성 및 필드 검증")
    void createPasswordUpdateRequestDto_Test(){
        // Given
        String password = "currentPassword";
        String changePassword = "newPassword";
        String changePasswordCheck = "newPassword";

        // When
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto(password, changePassword, changePasswordCheck);

        // Then
        assertThat(passwordUpdateRequestDto.getPassword()).isEqualTo(password);
        assertThat(passwordUpdateRequestDto.getChangePassword()).isEqualTo(changePassword);
        assertThat(passwordUpdateRequestDto.getChangePasswordCheck()).isEqualTo(changePasswordCheck);
    }

}
