package com.sns.room.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.like.entity.Like;
import com.sns.room.like.repository.LikeRepository;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "user@email.com", "Example123", UserRoleEnum.USER);
        ReflectionTestUtils.setField(testUser, "id", 1L);
    }

    @Test
    @DisplayName("좋아요 생성 성공 테스트")
    void createLikeTest() {
        // Given
        Long postId = 1L;
        Post testpost = new Post(
            new PostRequestDto(1L, "title", "content", "photo", LocalDateTime.now()),
            testUser);
        given(postService.findPost(postId)).willReturn(testpost);

        // When
        likeService.createLike(postId, testUser);

        // Then
        then(likeRepository).should(times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("한 번 더 좋아요를 누를 경우, 좋아요 생성 실패 테스트")
    void createLikeFailTest() {
        // Given
        Long postId = 1L;
        String resultMessage = "이미 좋아요를 눌렀습니다.";
        Like testlike = new Like(postId, testUser.getId());

        given(likeRepository.findByUserIdAndPostId(testUser.getId(), postId)).willReturn(
            Optional.of(testlike));

        // When
        InvalidInputException exception = assertThrows(InvalidInputException.class,
            () -> likeService.createLike(postId, testUser));

        // Then
        assertThat(resultMessage).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소 성공 테스트")
    void deleteLikeTest() {
        // Given
        Long postId = 1L;
        Like testlike = new Like(postId, testUser.getId());

        given(likeRepository.findByUserIdAndPostId(testUser.getId(), postId)).willReturn(
            Optional.of(testlike));

        // When
        likeService.deleteLike(postId, testUser);

        // Then
        then(likeRepository).should(times(1)).delete(any(Like.class));
    }

    @Test
    @DisplayName("다시 좋아요 취소를 시도할 경우, 좋아요 취소 실패 테스트")
    void deleteLikeFailTest() {
        // Given
        Long postId = 1L;
        String resultMessage = "이미 좋아요 취소를 했습니다.";
        given(likeRepository.findByUserIdAndPostId(testUser.getId(), postId)).willReturn(
            Optional.empty());

        // When
        InvalidInputException exception = assertThrows(InvalidInputException.class,
            () -> likeService.deleteLike(postId, testUser));

        // Then
        assertThat(resultMessage).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("팔로우 개수 조회 성공 테스트")
    void countLikesTest() {
        // given
        Long postId = 1L;
        Post testpost = new Post(
            new PostRequestDto(1L, "title", "content", "photo", LocalDateTime.now()),
            testUser);

        given(postService.findPost(postId)).willReturn(testpost);
        given(likeRepository.countByPostId(postId)).willReturn(1L);

        // When
        Long countLikes = likeService.countLikes(postId); // 좋아요 갯수

        // Then
        assertThat(countLikes).isEqualTo(1L);
    }
}
