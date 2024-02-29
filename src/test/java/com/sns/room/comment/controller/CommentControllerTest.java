
package com.sns.room.comment.controller;


import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.service.CommentService;
import com.sns.room.comment.test.ControllerTest;
import com.sns.room.notification.service.NotificationService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends ControllerTest {


    @MockBean
    private NotificationService notificationService;
    @MockBean
    private CommentService commentService;


    @Test
    @DisplayName("댓글 생성 요청")
    void createComment() throws Exception {
        //given
        String comment = "comment";
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment(comment);

        String postTitle = "postTitle";
        String username = "username";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();

        CommentResponseDto responseDto = new CommentResponseDto(postTitle, username, comment,
            createdAt, modifiedAt);

        given(
            commentService.createComment(commentRequestDto, TEST_POST_ID, TEST_USER_ID)).willReturn(
            responseDto);

        //when
        var action = mockMvc.perform(post("/posts/{postId}/comments", TEST_POST_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequestDto)));

        //then
        action.andExpect(status().isOk())
            .andDo(print());
    }


    @Nested
    @DisplayName("댓글 수정 요청")
    class UpdateComment {

        @Test
        @DisplayName("댓글 수정 요청 성공")
        void updateComment_Success() throws Exception {
            //given
            String comment = "comment";
            CommentRequestDto commentRequestDto = new CommentRequestDto();
            commentRequestDto.setComment(comment);

            String postTitle = "postTitle";
            String username = "username";
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime modifiedAt = LocalDateTime.now();
            CommentResponseDto commentResponseDto = new CommentResponseDto(postTitle, username,
                comment, createdAt, modifiedAt);
            given(commentService.updateComment(commentRequestDto, TEST_POST_ID, TEST_USER_ID,
                TEST_COMMENT_ID)).willReturn(commentResponseDto);

            //when
            var action = mockMvc.perform(
                put("/posts/{postId}/comments/{commentId}", TEST_POST_ID, TEST_COMMENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            //then
            action.andExpect(status().isOk())
                .andDo(print());
        }


        @Test
        @DisplayName("댓글 수정 요청 실패")
        void updateComment_Fail() throws Exception {
            CommentRequestDto commentRequestDto = new CommentRequestDto();
            commentRequestDto.setComment("comment");

            var action = mockMvc.perform(
                put("/posts/{postId}/comments/{commentId}", TEST_POST_ID, TEST_COMMENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            action.andExpect(status().isOk())
                .andDo(print());
        }
    }


    @Nested
    class DeleteComment {

        @Test
        @DisplayName("댓글 삭제 요청")
        void deleteComment() throws Exception {
            CommentRequestDto commentRequestDto = new CommentRequestDto();
            commentRequestDto.setComment("comment");

            var action = mockMvc.perform(
                delete("/posts/{postId}/comments/{commentId}", TEST_POST_ID, TEST_COMMENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentRequestDto)));

            action.andExpect(status().isOk())
                .andDo(print());
        }
    }


    @Nested
    class GetComment {

        @Test
        @DisplayName("댓글 조회 요청")
        void getAllComment() throws Exception {
            var action = mockMvc.perform(get("/posts/{postId}/comments", TEST_POST_ID));

            action.andExpect(status().isOk())
                .andDo(print());
        }
    }

}

