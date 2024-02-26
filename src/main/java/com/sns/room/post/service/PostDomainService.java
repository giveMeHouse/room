package com.sns.room.post.service;


import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostDomainService {

    //포스트 레포지토리 주입
    private final PostRepository postRepository;


    public void savePost(Post savePost) {
        postRepository.save(savePost);
    }


    //Post Id 검증 메서드
    public PostResponseDto getPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(postId + "를 찾을수 없습니다."));
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }

    //게시글 전체조회 검증 메서드
    public List<PostResponseDto> findAllPost() {
        List<Post> postList = postRepository.findAllWithUser();
        return postList.stream().map(post -> new PostResponseDto(post))
                .collect(Collectors.toList());
    }

    //게시글 삭제 검증 메서드
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

    //게시글 수정 검증
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

    public List<Post> findByUserId(Long id) {
        return postRepository.findByUserId(id);
    }
}

