package com.sns.room.comment.test;

import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import java.time.LocalDateTime;

public class PostTest implements CommonTest {
    Long TEST_TODO_ID = 1L;
    String TEST_TODO_TITLE = "title";
    String TEST_TODO_CONTENT = "content";
    String TEST_PHOTO = "photo";
    LocalDateTime TEST_MODIFIEDAT = LocalDateTime.now();

    PostRequestDto TEST_POST_REQUEST_DTO = new PostRequestDto(TEST_TODO_TITLE, TEST_TODO_CONTENT, TEST_PHOTO,TEST_MODIFIEDAT);
    PostRequestDto TEST_ANOTHER_POST_DTO = new PostRequestDto(ANOTHER_PREFIX+TEST_TODO_TITLE, ANOTHER_PREFIX+TEST_TODO_CONTENT, ANOTHER_PREFIX+TEST_PHOTO,TEST_MODIFIEDAT);

    Post TEST_POST = new Post(TEST_POST_REQUEST_DTO,TEST_USER);
    PostResponseDto TEST_POST_RESPONSE_DTO = new PostResponseDto(TEST_POST);

    Post TEST_ANOTHER_TODO = new Post(TEST_ANOTHER_POST_DTO, TEST_USER);
}
