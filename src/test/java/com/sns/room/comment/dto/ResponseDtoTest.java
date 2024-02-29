package com.sns.room.comment.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseDtoTest {

    @Test
    @DisplayName("응답 Dto 생성 성공")
    void createResponseDto_Test() {
        //given
        String postTitle = "postTitle";
        String username = "username";
        String comment = "comment";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();
        CommentResponseDto data = new CommentResponseDto(postTitle, username, comment, createdAt,
            modifiedAt);
        ResponseDto responseDto = new ResponseDto(data);

        //then
        assertEquals(responseDto.getData(), data);
    }

}