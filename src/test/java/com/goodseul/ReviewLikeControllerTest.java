package com.goodseul;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import data.controller.ReviewLikeController;
import data.dto.ReviewLikeDto;
import data.exception.UserNotFoundException;
import data.repository.ReviewRepository;
import data.repository.UserRepository;
import data.service.ReviewLikeService;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class ReviewLikeControllerTest {
    private MockMvc mockMvc;

    @MockBean
    ReviewLikeService reviewLikeService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    HttpServletRequest request;

    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    ReviewLikeController reviewLikeController;


    @Test
    void insertLike_WithNonExistentReview_ShouldThrowUserNotFoundException() {
        // given
        ReviewLikeDto dto = new ReviewLikeDto();
        dto.setR_idx(1); // 존재하지 않는 리뷰 ID

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.extractIdxFromRequest(request)).thenReturn(10L);
        log.info(request.toString() + "토큰값 임의 지정");

        // when & then
        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            reviewLikeController.insertLike(dto, request); // reviewLikeController 호출
        });

        // 예외 메시지 확인
        assertEquals("해당 사용자를 찾을 수 없습니다", thrownException.getMessage());
    }
    @Test
    void insertLike_WhenAllConditionsAreMet_ShouldReturnOk() {
        // given
        ReviewLikeDto dto = new ReviewLikeDto();
        dto.setR_idx(300);

        // HttpServletRequest를 Mocking하여 jwtService.extractIdxFromRequest 호출 시 원하는 값을 반환
        when(jwtService.extractIdxFromRequest(request)).thenReturn(3000L);

        // when
        ResponseEntity<String> response = reviewLikeController.insertLike(dto, request);
        log.info("리턴된 코드 : {} ",response.getStatusCode());

        // then
        assertEquals(OK, response.getStatusCode());
        verify(reviewLikeService).insertLike(dto, request);

    }
    @Test
    void deleteLike_WhenAllConditionsAreMet_ShouldReturnOk() {
        // given
        int r_idx = 1;

        when(jwtService.extractIdxFromRequest(request)).thenReturn(13L);

        // when
        ResponseEntity<Object> response = reviewLikeController.deleteLike(r_idx, request);

        // then
        assertEquals(OK, response.getStatusCode());
        verify(reviewLikeService).deleteLike(r_idx, request);
    }

}
