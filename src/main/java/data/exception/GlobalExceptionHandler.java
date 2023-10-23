package data.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        log.debug("해당 유저가 존재하지 않습니다.", userNotFoundException);
        return new ResponseEntity<>("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity<String> handleTokenExpiredException(TokenExpiredException tokenExpiredException) {
        log.debug("토큰이 만료되었습니다.", tokenExpiredException);
        return new ResponseEntity<>("인증 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenException.class)
    public final ResponseEntity<String> handleTokenException(TokenException tokenException) {
        log.debug("토큰이 존재하지않거나, 토큰의 내용에 문제가 있습니다.",tokenException);
        return new ResponseEntity<>("토큰이 존재하지않거나, 토큰의 내용에 문제가 있습니다.",HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(UnauthenticatedUserException unauthenticatedUserException) {
        log.debug("해당 리소스에 대한 접근 권한이 없습니다.", unauthenticatedUserException);
        return new ResponseEntity<>("해당 리소스에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> handleWrongPasswordException(WrongPasswordException wrongPasswordException) {
        log.debug("패스워드가 옳지 않습니다.", wrongPasswordException);
        return new ResponseEntity<>("패스워드가 옳지 않습니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public final ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException duplicateEmailException){
        log.debug("중복된 이메일입니다.",duplicateEmailException);
        return new ResponseEntity<>("중복된 이메일입니다.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public final ResponseEntity<String> handleDuplicateNicknameException(DuplicateNicknameException duplicateNicknameException) {
        log.debug("중복된 닉네임입니다.", duplicateNicknameException);
        return new ResponseEntity<>("중복된 닉네임입니다.",HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ImageRoadFailedException.class)
    public final ResponseEntity<String> handleImageRoadFailedException(ImageRoadFailedException imageRoadFailedException){
        log.debug("이미지 로드에 실패하였습니다.",imageRoadFailedException);
        return new ResponseEntity<>("이미지 로드에 실패하였습니다.",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException maxUploadSizeExceededException) {
        log.debug("허용된 용량을 초과한 파일입니다.", maxUploadSizeExceededException);
        return new ResponseEntity<>("허용된 용량을 초과한 파일입니다.",HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(IllegalMimeTypeException.class)
    public final ResponseEntity<String> handleIllegalMimeTypeException(IllegalMimeTypeException illegalMimeTypeException) {
        log.debug("올바르지 않은 확장자입니다.", illegalMimeTypeException);
        return new ResponseEntity<>("올바르지 않은 확장자입니다.",HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
