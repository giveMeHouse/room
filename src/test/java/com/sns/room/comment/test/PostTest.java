package com.sns.room.comment.test;

import com.sns.room.post.dto.PostRequestDto;
import com.sns.room.post.dto.PostResponseDto;
import com.sns.room.post.entity.Post;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class PostTest implements CommonTest {
    Long TEST_ANOTHER_POST_ID = 2L;
    String TEST_POST_TITLE = "title";
    String TEST_POST_CONTENT = "content";
    String TEST_PHOTO = "photo";
    LocalDateTime TEST_MODIFIEDAT = LocalDateTime.now();

    PostRequestDto TEST_POST_REQUEST_DTO = new PostRequestDto(TEST_POST_TITLE, TEST_POST_CONTENT, TEST_PHOTO,TEST_MODIFIEDAT);
    PostRequestDto TEST_ANOTHER_POST_DTO = new PostRequestDto(ANOTHER_PREFIX+TEST_POST_TITLE, ANOTHER_PREFIX+TEST_POST_CONTENT, ANOTHER_PREFIX+TEST_PHOTO,TEST_MODIFIEDAT);

    Post TEST_POST = new Post(TEST_POST_REQUEST_DTO,TEST_USER);

    PostResponseDto TEST_POST_RESPONSE_DTO = new PostResponseDto(TEST_POST);

    Post TEST_ANOTHER_TODO = new Post(TEST_ANOTHER_POST_DTO, TEST_USER);
}
