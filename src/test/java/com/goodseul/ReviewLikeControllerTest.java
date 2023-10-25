package com.goodseul;

import data.controller.ReviewController;
import data.service.ReviewLikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@Slf4j
public class ReviewLikeControllerTest {
    private MockMvc mockMvc;

    @MockBean
    ReviewLikeService reviewLikeService;

    @Autowired
    ReviewController reviewController;

    @Test
    public void insertLike_WhenUserExists () {
        // given


    }





}
