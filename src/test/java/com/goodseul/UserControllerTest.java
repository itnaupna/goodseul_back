package com.goodseul;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import data.controller.UserController;
import data.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;

@SpringBootTest
@Slf4j
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    void findId_WhenUserExists() {
        // given
        String name = "컁컁";
        String phone = "01066376620";
        String birth = "19951028";
        String expectedId = "jae@jae";

        given(userService.findByEmail(name, phone, birth)).willReturn(expectedId);

        // when
        ResponseEntity<String> response = userController.findByEmail(name, phone, birth);

        // then
        log.info("리턴된 코드 : {} ",response.getStatusCode());
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedId, response.getBody());
        verify(userService).findByEmail(name, phone, birth);
    }

    @Test
    void findId_WhenUserDoesNotExist() {
        // given
        String name = "존재하지않음";
        String phone = "01000000000";
        String birth = "20000101";

        given(userService.findByEmail(name, phone, birth)).willThrow(new EntityNotFoundException("User not found"));

        // when
        ResponseEntity<String> response = userController.findByEmail(name, phone, birth);

        // then
        log.info("리턴된 코드 : {} ",response.getStatusCode());
        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(userService).findByEmail(name, phone, birth);

    }
//    @Test
//    void findId_WhenUserList(){
//        String
//    }
}