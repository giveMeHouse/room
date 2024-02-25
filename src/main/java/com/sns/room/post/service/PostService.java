package com.sns.room.post.service;


import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    //4레이어드로 수정해보기
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    //게시글 생성
    public PostResponseDto createPost(PostRequestDto requestDto, Long userId) {
        //유저 정보 임의로 가져오기 여기서는 1번 아이디 유저
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        //객체 생성
        Post newPost = new Post(requestDto, user);
        //post 저장
        postRepository.save(newPost);
        //dto 반환
        PostResponseDto postResponseDto = new PostResponseDto(newPost);
        return postResponseDto;
    }
    //게시글 전체 조회
    public List<PostResponseDto> findAllPost() {
        List<Post> postList = postRepository.findAll();
        return postList.stream().map(post -> new PostResponseDto(post))
            .collect(Collectors.toList());
    }
    //게시글 선택 조회
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다"));
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }
    //게시글 삭제
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다"));
        postRepository.delete(post);
    }
    //게시글 업데이트
    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(Long postId,PostRequestDto requestDto,Long userId) {
        Post updatePost = postRepository.findById(postId).orElseThrow(()->new IllegalArgumentException(postId + "를 찾을수 없습니다."));
        Long updatePostId =updatePost.getUser().getId();
        if(!updatePostId.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        else {
            updatePost.updatePost(requestDto.getTitle(),requestDto.getContent());

        }
        return ResponseEntity.ok(new PostResponseDto(updatePost));
    }
}