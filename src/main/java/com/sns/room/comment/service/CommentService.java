package com.sns.room.comment.service;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.entity.Comment;
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.global.exception.InvalidPostException;
import com.sns.room.global.exception.InvalidUserException;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, long postId,
        long userId) {
        Post post = checkPost(postId);

        User user = checkUser(userId);

        Comment comment = new Comment(commentRequestDto.getComment(), post, user);

        commentRepository.save(comment);

        return new CommentResponseDto(comment.getPost().getTitle(), comment.getUser().getUsername(),
            comment.getComment());
    }


    // 존재하는 게시글인지 검증
    private Post checkPost(long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new InvalidPostException("존재하지 않는 게시글입니다."));
        return post;
    }

    // 존재하는 유저인지 검증
    private User checkUser(long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new InvalidUserException("존재하지 않는 유저입니다."));
        return user;
    }


}
