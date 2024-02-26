package com.sns.room.post.business;


import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostDomainService;
import com.sns.room.user.entity.User;
import com.sns.room.user.service.UserDomainService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    //4레이어드로 수정해보기
    private final PostDomainService postDomainService;
    private final UserDomainService userDomainService;

    //게시글 생성
    public PostResponseDto createPost(PostRequestDto requestDto, Long userId) {
        //유저 객체 생성
        User user = userDomainService.toEntity(userId);
        //게시글 객체 생성
        Post newPost = new Post(requestDto, user);
        //post 저장
        postDomainService.savePost(newPost);
        //dto 반환
        PostResponseDto postResponseDto = new PostResponseDto(newPost);
        return postResponseDto;
    }

    //게시글 전체 조회
    public List<PostResponseDto> findAllPost() {
        List<PostResponseDto> postList = postDomainService.findAllPost();
        return postList;
    }

    //게시글 선택 조회
    public PostResponseDto getPost(Long postId) {
        PostResponseDto postResponseDto = postDomainService.getPostId(postId);
        return postResponseDto;
    }
    //게시글 삭제
    @Transactional
    public void delete(Long postId, Long userId) {
        postDomainService.delete(postId, userId);
    }

    //게시글 업데이트
    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(Long postId, PostRequestDto requestDto,
        Long userId) {
        return postDomainService.updatePost(postId,requestDto,userId);
    }
}
