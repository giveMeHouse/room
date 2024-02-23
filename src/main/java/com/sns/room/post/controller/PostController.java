package com.sns.room.post.controller;


import com.sns.room.post.dto.CommonResponse;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController{
    private final PostService postService;
    //게시글 등록
    @PostMapping("/posts")
    public ResponseEntity<CommonResponse<PostResponseDto>> createPost(@RequestBody PostRequestDto requestDto) {
        Long userId= 1L;
        return ResponseEntity.ok(postService.createPost(requestDto,userId));
    }
    //게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<CommonResponse<List<PostResponseDto>>> getAllPost(){
        List<PostResponseDto> postList = postService.findAllPost();
        CommonResponse<List<PostResponseDto>> response = new CommonResponse<>("게시글 조회에 성공 했습니다.",postList);
        return ResponseEntity.ok(response);
    }
    //게시글 선택 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommonResponse<PostResponseDto>> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

}
