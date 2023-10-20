package data.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TestException.class)
    public final ResponseEntity<String> handleTestException(TestException testException){
        log.debug("테스트 예외입니다.", testException);
        return new ResponseEntity<>("테스트 예외입니다.", HttpStatus.CONFLICT);
    }
}
