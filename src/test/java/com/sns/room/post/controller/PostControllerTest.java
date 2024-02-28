
package com.sns.room.post.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@WebMvcTest(controllers = PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    private final LocalDateTime fake = LocalDateTime.now();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    ObjectMapper objectMapper;
    User user;

    @BeforeEach
    void setUp() {
        user = new User("test","test@test.com","test",UserRoleEnum.USER);
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(user);
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        mockMvc = webAppContextSetup(context).build();
    }


    @Test
    @DisplayName("게시글 생성")
    @WithMockUser
    void createPost() throws Exception {
        // 유저 인증 설정
        // given
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);
        PostResponseDto postResponseDto = new PostResponseDto(post);
        given(postService.createPost(any(PostRequestDto.class), any(Long.class))).willReturn(
            postResponseDto);

        // when & then
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDto)))
            .andExpect(status().isOk());
    }


    @Test
    @DisplayName("게시글 전체 조회")
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllPost() throws Exception {
        //상태 메세지
        MockMvc mockMvc = webAppContextSetup(context)
            .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필요한 필터 추가
            .alwaysDo(print()) // 모든 요청/응답에 대해 로그를 출력
            .build();


        //given
        List<PostResponseDto> postDtoList = new ArrayList<>();
        PostRequestDto postRequestDto = new PostRequestDto(1L,"zz", "zzz", "zzz", fake);
        User user1 = new User("test", "test@test", "test", UserRoleEnum.USER);
        PostRequestDto postRequestDto2 = new PostRequestDto(2L,"zz2", "zzz2", "zzz2", fake);
        User user2 = new User("test2", "test2@test", "test", UserRoleEnum.USER);
        Post post = new Post(postRequestDto, user1);
        Post post2 = new Post(postRequestDto2, user2);
        PostResponseDto test1 = new PostResponseDto(post);
        PostResponseDto test2 = new PostResponseDto(post2);
        postDtoList.add(test1);
        postDtoList.add(test2);
        given(postService.findAllPost()).willReturn(postDtoList);
        //when
        mockMvc.perform(get("/posts"))
            .andExpect(status().isOk());

    }

    @Test
    @DisplayName("게시글 선택 조회")
    @WithMockUser(username = "user", roles = {"USER"})
    void getPost() throws Exception {
        //상태 메세지
        MockMvc mockMvc = webAppContextSetup(context)
            .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필요한 필터 추가
            .alwaysDo(print()) // 모든 요청/응답에 대해 로그를 출력
            .build();
        //given
        PostRequestDto postRequestDto = new PostRequestDto(1L,"zz", "zzz", "zzz", fake);
        User user1 = new User("test", "test@test", "test", UserRoleEnum.USER);
        Post post = new Post(postRequestDto, user1);
        PostResponseDto test1 = new PostResponseDto(post);
        given(postService.getPost(1L)).willReturn(test1);
        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제")
    @WithMockUser
    void deletePost() throws Exception {
        // 유저 인증 설정
        // given
        PostRequestDto postRequestDto = new PostRequestDto(1L,"title", "content", "category", fake);
        Post post = new Post(postRequestDto, user);
        Long postId = 1L;
        PostResponseDto postResponseDto = new PostResponseDto(post);
        doNothing().when(postService).delete(any(Long.class), any(Long.class));


        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                .param("userId", String.valueOf(user.getId())))
            .andExpect(status().isOk());
        verify(postService).delete(any(Long.class), any(Long.class));
    }
    @Test
    @DisplayName("게시글 수정")
    @WithMockUser
    void updatePost() throws Exception {
        // 유저 인증 설정
        // given
        PostRequestDto postRequestDto1 = new PostRequestDto(1L,"zz", "zz", "category", fake);
        PostRequestDto postRequestDto2 = new PostRequestDto(1L,"수정", "테스트", "category", fake);
        Post originalPost = new Post(postRequestDto1, user);
        Long postId = 1L;
        PostResponseDto postResponseDto = new PostResponseDto(originalPost);

        given(postService.updatePost(eq(postId), any(PostRequestDto.class), eq(user.getId())))
            .willReturn(ResponseEntity.ok(postResponseDto));

        // when & then
        mockMvc.perform(put("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDto2)))
            .andExpect(status().isOk());

        // 서비스 메소드 호출 검증을 위한 Argument 설정
        ArgumentCaptor<PostRequestDto> postRequestDtoCaptor = ArgumentCaptor.forClass(PostRequestDto.class);

        // 서비스 메소드 호출 검증
        verify(postService).updatePost(eq(postId), postRequestDtoCaptor.capture(), eq(user.getId()));

        // 캡처된 객체와 예상 값을 비교
        PostRequestDto capturedRequest = postRequestDtoCaptor.getValue();
        System.out.println("Captured Title: " + capturedRequest.getTitle());
        System.out.println("Expected Title: "+capturedRequest.getTitle());
        assertEquals(postRequestDto2.getTitle(), capturedRequest.getTitle());

        System.out.println("Captured Content: " + capturedRequest.getContent());
        System.out.println("Expected Content: ,"+capturedRequest.getContent());
        assertEquals(postRequestDto2.getContent(), capturedRequest.getContent());
    }

}
