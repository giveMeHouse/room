
package com.sns.room.comment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.comment.service.CommentService;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private CommentRepository commentRepository;

    User user;


    @BeforeEach
    void setUp() {
        user = new User("test", "test@test.com", "test", UserRoleEnum.USER);
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(user);
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        mockMvc = webAppContextSetup(context).build();
    }
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
                    delete("/posts/{postId}/comments/{commentId}", "1", "1")
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
                var action = mockMvc.perform(get("/posts/{postId}/comments", "1"));

                action.andExpect(status().isOk())
                    .andDo(print());
            }
        }

    }
