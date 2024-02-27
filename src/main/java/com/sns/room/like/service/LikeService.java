package com.sns.room.like.service;

import com.sns.room.global.exception.InvalidInputException;
import com.sns.room.like.entity.Like;
import com.sns.room.like.repository.LikeRepository;
import com.sns.room.post.service.PostDomainService;
import com.sns.room.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostDomainService postDomainService;

    @Transactional
    public void createLike(Long postId, User user) {
        postDomainService.findPost(postId);
        if (!likeRepository.findByUserIdAndPostId(user.getId(), postId).isEmpty()) {
            throw new InvalidInputException("이미 좋아요를 눌렀습니다.");
        }
        Like like = new Like(postId, user.getId());
        likeRepository.save(like);
    }
}
