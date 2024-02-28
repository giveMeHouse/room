package com.sns.room.comment.service;

import com.sns.room.comment.dto.CommentRequestDto;
import com.sns.room.comment.dto.CommentResponseDto;
import com.sns.room.comment.entity.Comment;
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.global.exception.InvalidCommentException;
import com.sns.room.global.exception.InvalidPostException;
import com.sns.room.global.exception.InvalidUserException;
import com.sns.room.post.entity.Post;
import com.sns.room.post.repository.PostRepository;
import com.sns.room.user.entity.User;
import com.sns.room.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, long postId,
        long userId) {
        Post post = checkPost(postId);
        System.out.println(post+"포스트를 확인해주세요");
        User user = checkUser(userId);

        Comment comment = new Comment(commentRequestDto.getComment(), post, user);

        commentRepository.save(comment);

        return new CommentResponseDto(comment.getPost().getTitle(), comment.getUser().getUsername(),
            comment.getComment(), comment.getCreatedAt(), comment.getModifiedAt());
    }

    @Transactional
    public CommentResponseDto updateComment(CommentRequestDto commentRequestDto, long postId,
        long userId, long commentId) {
        Post post = checkPost(postId);

        User user = checkUser(userId);

        Comment comment = checkComment(commentId);

        isValidPost(post, comment);

        isValidUser(user, comment);

        comment.Update(commentRequestDto.getComment());

        return new CommentResponseDto(comment.getPost().getTitle(), comment.getUser().getUsername(),
            comment.getComment(), comment.getCreatedAt(), comment.getModifiedAt());
    }


    @Transactional
    public CommentResponseDto deleteComment(long postId, long userId, long commentId) {
        Post post = checkPost(postId);

        User user = checkUser(userId);

        Comment comment = checkComment(commentId);

        isValidPost(post, comment);

        isValidUser(user, comment);

        comment.SoftDeleted();

        return new CommentResponseDto(comment.getPost().getTitle(), comment.getUser().getUsername(),
            comment.getComment(), comment.getCreatedAt(), comment.getModifiedAt());
    }

    public List<CommentResponseDto> getAllComment() {
        List<Comment> commentList = commentRepository.findAll();
        List<CommentResponseDto> responseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            responseDtoList.add(new CommentResponseDto(comment.getPost().getTitle(),
                comment.getUser().getUsername(), comment.getComment(), comment.getCreatedAt(), comment.getModifiedAt()));
        }

        return responseDtoList;
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

    // 존재하는 댓글인지 검증
    private Comment checkComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
            InvalidCommentException::new);
        return comment;
    }

    // 댓글이 달려있는 게시글인지 검증
    private boolean isValidPost(Post post, Comment comment) {
        if (!post.getId().equals(comment.getPost().getId())) {
            throw new InvalidPostException("올바르지 않은 게시글입니다.");
        }
        return true;
    }

    // 댓글을 수정/삭제할 권한이 있는 유저인지 검증
    private boolean isValidUser(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new InvalidUserException("작성자만 수정/삭제가능합니다.");
        }
        return true;
    }
}
