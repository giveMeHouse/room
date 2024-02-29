package com.sns.room.comment.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sns.room.comment.controller.CommentController;
import com.sns.room.global.jwt.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(CommentController.class)
public class ControllerTest implements CommonTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();

        // Mock 테스트 UserDetails 생성
        UserDetailsImpl testUserDetails = new UserDetailsImpl(TEST_USER);
        ReflectionTestUtils.setField(TEST_USER,"id",1L);

        // SecurityContext 에 인증된 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                testUserDetails, testUserDetails.getPassword(), testUserDetails.getAuthorities()));
    }
}
