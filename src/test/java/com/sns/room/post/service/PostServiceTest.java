package com.sns.room.post.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import com.sns.room.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@MockBean(JpaMetamodelMappingContext.class)
@SpringBootTest
//@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository;
    @MockBean
    MockMvc mockMvc;
    User user;
    @Autowired
    WebApplicationContext context;
    @Mock
    UserRepository userRepository;
    private LocalDateTime fake = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        user = new User(1L,"test","test@test.com","test", UserRoleEnum.USER);
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(user);
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("게시글 전체 조회")
    void getAllPost() {
        // given
        List<Post> postList = new ArrayList<>();
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);
        PostRequestDto postRequestDto2 = new PostRequestDto(2L,"title2", "content2", "category", fake);
        Post post2 = new Post(postRequestDto2, user);
        postList.add(post);
        postList.add(post2);


        given(postRepository.findAllWithUser()).willReturn(postList);
        // when
        List<Post> foundPosts = postRepository.findAllWithUser();
        // then
        assertNotNull(foundPosts);
        assertFalse(foundPosts.isEmpty());
        assertEquals(2, foundPosts.size());
        assertEquals(post.getId(), foundPosts.get(0).getId());
        assertEquals(post.getTitle(), foundPosts.get(0).getTitle());
        assertEquals(post.getContent(), foundPosts.get(0).getContent());
    }

    @Test
    @DisplayName("게시글 선택 조회")
    void getPost() {
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        PostResponseDto foundPost = postService.getPost(1L);
        //then
        assertNotNull(foundPost);
        assertEquals(post.getId(), foundPost.getId());
        assertEquals(post.getTitle(), foundPost.getTitle());
        assertEquals(post.getContent(), foundPost.getContent());
    }


    @Test
    @DisplayName("게시글 생성")
    void createPost() {
        //given
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = Post.builder()
            .title("New Post")
            .content("This is a new post.")
            .photo("News")
            .user(user)
            .build();


        given(postRepository.save(any(Post.class))).willReturn(post);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(any(Long.class))).willReturn(Optional.of(user));
        // when

        PostResponseDto savedPost = postService.createPost(postRequestDto, user.getId());

        // then
        //assertEquals(testId, savedPost.getId());
        assertEquals("title", savedPost.getTitle());
        assertEquals("content", savedPost.getContent());
    }
    @Test
    @DisplayName("게시글 삭제")
    void deletePost() {
        // given
        Long postId = 1L;
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.delete(postId, user.getId());

        // then
        verify(postRepository).delete(post);
    }


    @Test
    @DisplayName("게시글 수정")
    void updatePost() {
        // given
        Long postId = 1L;
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);
        //수정할 내용
        PostRequestDto updatePostRequestDto = new PostRequestDto(1L,"수정", "수정", "category", fake);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when
        postService.updatePost(postId, updatePostRequestDto, user.getId());

        //then
        assertEquals("수정", post.getTitle());
        assertEquals("수정", post.getContent());
    }

}
