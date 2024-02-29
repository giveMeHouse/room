package com.sns.room.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sns.room.follow.dto.FollowingResponseDto;
import com.sns.room.follow.entity.Follow;
import com.sns.room.follow.repository.FollowRepository;
import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.service.AuthService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
class FollowServiceTest {

    @InjectMocks
    FollowService followService;
    @Mock
    FollowRepository followRepository;
    @Mock
    private AuthService authService;
    @Mock
    private PostService postService;

    private Follow testFollow;
    private User testFromUser;
    private User testToUser;
    private Long toUserId;

    @BeforeEach
    void setUp() {
        testFromUser = new User("fromUser", "from@email.com", "Example123", UserRoleEnum.USER);
        testToUser = new User("toUser", "to@email.com", "Example123", UserRoleEnum.USER);
        ReflectionTestUtils.setField(testFromUser, "id", 1L);
        ReflectionTestUtils.setField(testToUser, "id", 2L);

        testFollow = Follow.builder()
            .fromUserId(testFromUser.getId())
            .toUserId(testToUser.getId())
            .build();
    }

    @Test
    @DisplayName("팔로우 생성 성공 테스트")
    void createFollowsTest() {
        // Given
        toUserId = 2L;
        given(authService.findUser(toUserId)).willReturn(testToUser); // 팔로우 할 유저

        // When
        followService.createFollow(testFromUser, toUserId);

        // Then
        then(followRepository).should(times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("본인을 팔로우할 경우 팔로우 실패 테스트")
    void createSelfFollowFailTest() {
        // Given
        toUserId = 1L;
        String resultMessage = "자신을 팔로우할 수 없습니다.";

        // When
        InvalidInputException exception = assertThrows(InvalidInputException.class,
            () -> followService.createFollow(testFromUser, toUserId));

        // Then
        assertThat(resultMessage).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("팔로우 취소 성공 테스트")
    void deleteFollowTest() {
        // Given, mock data 생성 목적
        toUserId = 2L;
        given(authService.findUser(toUserId)).willReturn(testToUser);
        given(followRepository.findByFromUserIdAndToUserId(testFromUser.getId(),
            testToUser.getId())).willReturn(Optional.of(testFollow));

        // When
        followService.deleteFollow(testFromUser, toUserId);

        // Then
        then(followRepository).should(times(1)).delete(any(Follow.class));
    }

    @Test
    @DisplayName("팔로우가 존재하지 않을 경우, 팔로우 취소 실패 테스트")
    void deleteFollowNotFindUserTest() {
        // Given
        toUserId = 404L;
        String resultMessage = "해당 팔로우를 찾을 수 없습니다.";

        // When
        InvalidInputException exception = assertThrows(InvalidInputException.class,
            () -> followService.deleteFollow(testFromUser, toUserId));

        // Then
        assertThat(resultMessage).isEqualTo(exception.getMessage());
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공 테스트")
    void getFollowingListTest() {
        // Given
        Long fromUserId = 1L;

        List<Follow> follows = new ArrayList<>();
        follows.add(testFollow);
        given(followRepository.findAllByFromUserId(fromUserId)).willReturn(follows);

        given(authService.findUser(fromUserId)).willReturn(testToUser);
        List<User> users = new ArrayList<>();
        users.add(testToUser);

        // When
        List<FollowingResponseDto> resultList = followService.getFollowingList(fromUserId);

        // Then
        assertEquals(resultList.size(), follows.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertThat(resultList.get(i).getToUserId()).isEqualTo(users.get(i).getId());
            assertThat(resultList.get(i).getUsername()).isEqualTo(users.get(i).getUsername());
        }
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공 테스트")
    void getFollowerListTest() {
        // Given
        Long toUserId = 1L;

        List<Follow> follows = new ArrayList<>();
        follows.add(testFollow);
        given(followRepository.findAllByFromUserId(toUserId)).willReturn(follows);

        given(authService.findUser(toUserId)).willReturn(testToUser);
        List<User> users = new ArrayList<>();
        users.add(testToUser);

        // When
        List<FollowingResponseDto> resultList = followService.getFollowingList(toUserId);

        // Then
        assertEquals(resultList.size(), follows.size());
        for (int i = 0; i < resultList.size(); i++) {
            assertThat(resultList.get(i).getToUserId()).isEqualTo(users.get(i).getId());
            assertThat(resultList.get(i).getUsername()).isEqualTo(users.get(i).getUsername());
        }
    }

    @Test
    @DisplayName("팔로워 게시글 목록 조회 성공 테스트")
    void getAllFollowingPostTest() {
        // Given
        List<Follow> follows = new ArrayList<>();
        follows.add(testFollow);

        Post post = new Post(
            new PostRequestDto(1L, "title", "content", "photo", LocalDateTime.now()),
            testFromUser);
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        given(followRepository.findAllByFromUserId(testFromUser.getId())).willReturn(follows);
        given(postService.findByUserId(2L)).willReturn(posts);

        // When
        List<PostResponseDto> result = followService.getAllFollowingPost(testFromUser);

        // Then
        assertThat(result.size()).isEqualTo(posts.size());
    }

    @Test
    @DisplayName("팔로워 목록에서 가장 최근에 팔로우한 사용자 찾기 성공 테스트")
    void findLatestUserTest() {
        // Given
        Long toUserId = 1L;
        given(followRepository.findFirstByToUserIdOrderByCreatedAtDesc(toUserId)).willReturn(
            Optional.of(testFollow));

        // When
        Follow result = followService.findLatestUser(toUserId);

        // Then
        assertNotNull(result);
        assertThat(result.getToUserId()).isEqualTo(testFollow.getToUserId());
    }

    @Test
    @DisplayName("가장 최근에 팔로우한 사용자 찾기 실패 테스트")
    void notFoundLatestUserTest() {
        // Given
        Long toUserId = 1L;
        given(followRepository.findFirstByToUserIdOrderByCreatedAtDesc(toUserId)).willReturn(
            Optional.empty());

        // When - Then
        assertThrows(IllegalArgumentException.class, () -> followService.findLatestUser(toUserId));
        then(followRepository).should(times(1))
            .findFirstByToUserIdOrderByCreatedAtDesc(toUserId);
    }
}
