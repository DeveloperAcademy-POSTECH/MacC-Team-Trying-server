package trying.cosmos.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static trying.cosmos.exception.ExceptionType.UNKNOWN_EXCEPTION;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionEntity> custom(CustomException e) {
        return new ResponseEntity<>(new CustomExceptionEntity(e.getError(), e), e.getError().getStatus());
    }

    @RestControllerAdvice
    private static class UnknownExceptionAdvice {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<CustomExceptionEntity> global(Exception e) {
            return new ResponseEntity<>(new CustomExceptionEntity(UNKNOWN_EXCEPTION, e), UNKNOWN_EXCEPTION.getStatus());
        }
    }
}
