package com.sns.room.comment.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.dto.ResponseDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseDtoTest {

    @Test
    @DisplayName("응답 Dto 생성 성공")
    void createResponseDto_Test(){
        //given
        String postTitle = "postTitle";
        String username = "username";
        String comment = "comment";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();
        CommentResponseDto data = new CommentResponseDto(postTitle,username,comment,createdAt,modifiedAt);
        String message = "message";
        ResponseDto responseDto = new ResponseDto(message, data);

        //then
        assertEquals(responseDto.getMessage(),message);
        assertEquals(responseDto.getData(),data);
    }

}