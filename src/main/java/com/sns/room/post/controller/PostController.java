package com.sns.room.post.controller;


import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController{
    private final PostService postService;
    //게시글 등록
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto) {
        Long userId= 1L;
        return ResponseEntity.ok(postService.createPost(requestDto,userId));
    }
    //게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getAllPost(){
        List<PostResponseDto> postList = postService.findAllPost();;
        return ResponseEntity.ok(postList);
    }
    //게시글 선택 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(postService.getPost(postId));
    }
    //게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            postService.delete(postId);
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("게시글 삭제 오류");
        }
    }
    //게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto) {
        Long userId = 1L;
        return postService.updatePost(postId, requestDto, userId);
    }

}
