package com.sns.room.comment.test;

import com.sns.room.user.entity.User;
import com.sns.room.user.entity.UserRoleEnum;

public interface CommonTest {
    String ANOTHER_PREFIX = "another-";
    Long TEST_COMMENT_ID = 1L;
    Long TEST_POST_ID = 1L;
    Long TEST_USER_ID = 1L;
    Long TEST_ANOTHER_USER_ID = 2L;
    String TEST_USER_NAME = "username";
    String TEST_USER_EMAIL = "test@gmail.com";
    String TEST_USER_PASSWORD = "password";
    User TEST_USER = new User(TEST_USER_NAME,TEST_USER_EMAIL,TEST_USER_PASSWORD, UserRoleEnum.USER);
    User TEST_ANOTHER_USER = new User(ANOTHER_PREFIX + TEST_USER_NAME, ANOTHER_PREFIX + TEST_USER_EMAIL, ANOTHER_PREFIX + TEST_USER_PASSWORD,UserRoleEnum.USER);

}
