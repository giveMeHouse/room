
package com.sns.room.comment.controller;

<<<<<<< HEAD
=======
import static org.mockito.BDDMockito.given;
>>>>>>> e9ea86afe974134c03a29b83391309047e619882
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sns.room.comment.dto.CommentRequestDto;
<<<<<<< HEAD
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.comment.service.CommentService;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
=======
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.service.CommentService;
import com.sns.room.comment.test.ControllerTest;
import com.sns.room.notification.service.NotificationService;
import java.time.LocalDateTime;
>>>>>>> e9ea86afe974134c03a29b83391309047e619882
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

<<<<<<< HEAD

    @BeforeEach
    void setUp() {
        user = new User("test", "test@test.com", "test", UserRoleEnum.USER);
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(user);
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));
=======
        String postTitle = "postTitle";
        String username = "username";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();
>>>>>>> e9ea86afe974134c03a29b83391309047e619882

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
<<<<<<< HEAD
//
//    @Nested
//    class CreateComment {
//
//        @Test
//        @DisplayName("댓글 생성 요청")
//        void createComment() throws Exception {
//            // Given
//            String comment = "comment";
//            String postTitle = "postTitle";
//            String username = "test@test.com";
//            CommentRequestDto commentRequestDto = new CommentRequestDto();
//            commentRequestDto.setComment(comment);
//
//
//            Post post = new Post(postTitle, "content", "photo", user);
//
//
//            LocalDateTime createdAt = LocalDateTime.now();
//            LocalDateTime modifiedAt = LocalDateTime.now();
//
//            CommentResponseDto responseDto = new CommentResponseDto(postTitle, username, comment,
//                createdAt, modifiedAt);
//
//            given(commentService.createComment(any(CommentRequestDto.class), any(Long.class),
//                any(Long.class))).willReturn(responseDto);
//            String jsonContent = objectMapper.writeValueAsString(responseDto);
//
//            // When
//            var action = mockMvc.perform(post("/posts/{postId}/comments", post.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(jsonContent));
//
//            // Then
//            action.andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("댓글 생성 성공"))
//                .andExpect(jsonPath("$.data.username").value(username))
//                .andDo(print());
//        }

        @Nested
        class UpdateComment {

            @Test
            @DisplayName("댓글 수정 요청")
            void updateComment() throws Exception {
                CommentRequestDto commentRequestDto = new CommentRequestDto();
                commentRequestDto.setComment("comment");

                var action = mockMvc.perform(put("/posts/{postId}/comments/{commentId}", "1", "1")
=======


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
>>>>>>> e9ea86afe974134c03a29b83391309047e619882
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
<<<<<<< HEAD
=======


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
>>>>>>> e9ea86afe974134c03a29b83391309047e619882
