package data.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @ExceptionHandler(BoardNotFoundException.class)
    public final ResponseEntity<String> BoardNotFoundException(BoardNotFoundException e){
        log.debug("dd",e);
        return new ResponseEntity<>("게시글을 찾을 수 없습니다",HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserConflictException.class)
    public final ResponseEntity<String> handleUserConflictException(UserConflictException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
    }

}
