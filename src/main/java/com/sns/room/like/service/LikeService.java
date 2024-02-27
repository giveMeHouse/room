package com.sns.room.like.service;

import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.like.entity.Like;
import com.sns.room.like.repository.LikeRepository;

import com.sns.room.post.service.PostService;
import com.sns.room.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;

    @Transactional
    public void createLike(Long postId, User user) {
        postService.findPost(postId);
        if (!likeRepository.findByUserIdAndPostId(user.getId(), postId).isEmpty()) {
            throw new InvalidInputException("이미 좋아요를 눌렀습니다.");
        }
        Like like = new Like(postId, user.getId());
        likeRepository.save(like);
    }

    public Long countLikes(Long postId) {
        postService.findPost(postId);
        return likeRepository.countByPostId(postId);
    }

    @Transactional
    public void deleteLike(Long postId, User user) {
        postService.findPost(postId);
        Like like = likeRepository.findByUserIdAndPostId(user.getId(), postId).orElseThrow(
            () -> new InvalidInputException("이미 좋아요 취소를 했습니다.")
        );
        likeRepository.delete(like);
    }
}
