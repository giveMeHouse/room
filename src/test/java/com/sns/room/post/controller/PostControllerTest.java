package com.sns.room.post.controller;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.post.service.PostService;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest{

    @MockBean
    private UserDetailsImpl userDetails;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    private final LocalDateTime fake = LocalDateTime.now();
    @Test
    @DisplayName("게시글 전체 조회")
    @WithMockUser(username="user", roles={"USER"})
    void getAllPost() throws Exception {
        //given
        List<PostResponseDto> postDtoList = new ArrayList<>();
        PostRequestDto postRequestDto = new PostRequestDto("zz", "zzz", "zzz", fake);
        User user = new User("test", "test@test", "test", UserRoleEnum.USER);
        PostRequestDto postRequestDto2 = new PostRequestDto("zz2", "zzz2", "zzz2", fake);
        User user2 = new User("test2", "test2@test", "test", UserRoleEnum.USER);
        Post post = new Post(postRequestDto, user);
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


    //게시글 선택 조회 성공
    @Test
    @DisplayName("게시글 한개만 조회")
    @WithMockUser(username="user", roles={"USER"})
    void getPost() throws Exception {
        //given
        PostRequestDto postRequestDto = new PostRequestDto("zz", "zzz", "zzz", fake);
        User user1 = new User("test", "test@test", "test", UserRoleEnum.USER);
        Post post = new Post(postRequestDto, user1);
        PostResponseDto postResponseDto = new PostResponseDto(post);
        given(postService.getPost(1L)).willReturn(postResponseDto);
        //when
        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk());

    }

    //게시글 선택 조회 실패
    @Test
    @DisplayName("게시글 한개만 조회 - 존재하지 않는 게시글")
    @WithMockUser(username="user", roles={"USER"})
    void getPostWhenPostNotFound() throws Exception {
        // given
        long postId = 1L; // 존재하지 않는 ID를 가정
        given(postService.getPost(postId)).willThrow(new NotFoundException("Post not found"));

        // when & then
        mockMvc.perform(get("/posts/" + postId))
            .andExpect(status().isNotFound()); // 404 Not Found 상태 코드를 기대
    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
    @Test
    @DisplayName("게시글 삭제 - 성공 케이스")
    @WithMockUser(username = "user", roles = {"USER"})
    void deletePostTestSuccess() throws Exception {
        // given
        Long postId = 1L; // 가정: 삭제하려는 게시글 ID
        Long userId = 1L; // 가정: 요청을 보낸 사용자 ID
        PostRequestDto postRequestDto = new PostRequestDto("zz", "zzz", "zzz", fake);
        User user = new User("user", "user@test.com", "password", UserRoleEnum.USER);
        Post post = new Post(postRequestDto, user);
        PostResponseDto postResponseDto = new PostResponseDto(post);
        user.setId(userId); // 사용자 ID 설정
        given(postService.getPost(1L)).willReturn(postResponseDto);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 사용자와 게시글이 존재하고, 사용자가 게시글의 소유자라고 가정할 때, 삭제 요청이 성공
        doNothing().when(postService).delete(postId, userId);

        // when & then
        mockMvc.perform(delete("/posts/{postId}", postId)
                .principal(() -> userDetails.getUser().getUsername())) // 요청을 보내는 사용자를 모킹
            .andExpect(status().isOk()) // HTTP 200 상태 코드 검증
            .andExpect(content().string("게시글이 성공적으로 삭제되었습니다."));

        // postService의 delete 메소드가 실제로 호출되었는지 검증
        verify(postService, times(1)).delete(postId, userId);
    }







}




