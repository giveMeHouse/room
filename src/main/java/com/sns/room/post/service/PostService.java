package com.sns.room.post.service;


import com.sns.room.post.dto.CommonResponse;
import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    //4레이어드로 수정해보기
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    //게시글 생성
    public CommonResponse<PostResponseDto> createPost(PostRequestDto requestDto, Long userId) {
        //유저 정보 임의로 가져오기 여기서는 1번 아이디 유저
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        //객체 생성
        Post newPost = new Post(requestDto, user);
        //post 저장
        postRepository.save(newPost);
        //dto 반환
        PostResponseDto postResponseDto = new PostResponseDto(newPost);
        return new CommonResponse<>("게시글이 등록되었습니다.", postResponseDto);
    }
    //게시글 전체 조회
    public List<PostResponseDto> findAllPost() {
        List<Post> postList = postRepository.findAll();
        return postList.stream().map(post -> new PostResponseDto(post))
            .collect(Collectors.toList());
    }

    //게시글 선택 조회
//    public CommonResponse<PostResponseDto> getPost(Long postId) {
//        Post post = postRepository.findById(postId);
//    }


}
