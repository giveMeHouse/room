package com.sns.room.comment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.entity.Comment;
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.comment.test.PostTest;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest extends PostTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("댓글 생성")
    void createComment() {
        //given
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setComment("comment");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        Post post = new Post(new PostRequestDto("title", "content", "photo", LocalDateTime.now()),
            user);

        given(postRepository.findById(TEST_POST_ID)).willReturn(Optional.of(post));
        given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(user));

        Comment comment = new Comment(requestDto.getComment(), post, user);

        //when
        CommentResponseDto commentResponseDto = commentService.createComment(requestDto,
            TEST_POST_ID, TEST_USER_ID);

        //then
        assertEquals(commentResponseDto.getComment(), comment.getComment());
        assertEquals(comment.getUser(), user);
        assertEquals(comment.getPost(), post);
    }

    @Test
    void updateComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void getAllComment() {
    }

    @Test
    void findLatestComment() {
    }
}