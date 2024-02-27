package com.sns.room.comment.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sns.room.comment.dto.CommentResponseDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentResponseDtoTest {

    @Test
    @DisplayName("댓글 반환 Dto 생성 성공")
    void createCommentResponseDto_Test(){
        //given
        String postTitle = "postTitle";
        String username = "username";
        String comment = "comment";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();
        CommentResponseDto commentResponseDto = new CommentResponseDto(postTitle, username, comment,createdAt,modifiedAt);


        //then
        assertThat(commentResponseDto.getPostTitle()).isEqualTo(postTitle);
        assertThat(commentResponseDto.getUsername()).isEqualTo(username);
        assertThat(commentResponseDto.getComment()).isEqualTo(comment);

    }

}