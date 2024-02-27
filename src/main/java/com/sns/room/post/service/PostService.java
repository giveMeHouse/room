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
        //유저 객체 생성
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        //게시글 객체 생성
        Post savePost = new Post(requestDto, user);
        //post 저장
        postRepository.save(savePost);
        //dto 반환
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }

    //게시글 전체 조회
    public List<PostResponseDto> findAllPost() {
        List<Post> postList = postRepository.findAllWithUser();
        List<PostResponseDto> postDtoList = postList.stream().map(post -> new PostResponseDto(post))
            .collect(Collectors.toList());
        return postDtoList;
    }

    //게시글 선택 조회
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다."));
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }

    //게시글 삭제
    @Transactional
    public void delete(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다"));
        if (userId.equals(post.getUser().getId())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("자신이 작성한 글만 삭제할수 있습니다.");
        }
    }

    //게시글 업데이트
    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(Long postId, PostRequestDto requestDto,
        Long userId) {
        Post updatePost = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다."));
        Long updatePostId = updatePost.getUser().getId();
        if (!updatePostId.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        } else {
            updatePost.updatePost(requestDto.getTitle(), requestDto.getContent());
        }
        return ResponseEntity.ok(new PostResponseDto(updatePost));
    }

}

