package trying.cosmos.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.NoSuchElementException;

import static trying.cosmos.exception.ExceptionType.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionEntity> custom(CustomException e) {
        CustomExceptionEntity exception = new CustomExceptionEntity(e.getError(), e.getMessage(), e);
        return new ResponseEntity<>(new CustomExceptionEntity(e.getError(), e.getMessage(), e), e.getError().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomExceptionEntity> validation(MethodArgumentNotValidException e) {
        return generalResponse(INVALID_INPUT, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomExceptionEntity> input(Exception e) {
        if (e.getMessage().contains("Enum")) {
            return generalResponse(NO_DATA, e);
        } else if (e.getMessage().contains("NumberFormat")) {
            return generalResponse(INVALID_TYPE, e);
        } else if (e.getMessage().contains("UnrecognizedPropertyException")) {
            return generalResponse(INVALID_INPUT, e);
        } else if (e.getMessage().contains("JSON")) {
            return generalResponse(INVALID_JSON_FORMAT, e);
        } else {
            return generalResponse(UNKNOWN_EXCEPTION, e);
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomExceptionEntity> parameter(MissingServletRequestParameterException e) {
        return generalResponse(INVALID_PARAMETER, e);
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MultipartException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<CustomExceptionEntity> header(Exception e) {
        return generalResponse(INVALID_HEADER, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomExceptionEntity> method(HttpRequestMethodNotSupportedException e) {
        return generalResponse(INVALID_METHOD, e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<CustomExceptionEntity> nodata(NoSuchElementException e) {
        return generalResponse(NO_DATA, e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomExceptionEntity> duplicated(DataIntegrityViolationException e) {
        return generalResponse(DUPLICATED, e);
    }

    @RestControllerAdvice
    private static class UnknownExceptionAdvice {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<CustomExceptionEntity> global(Exception e) {
            return generalResponse(UNKNOWN_EXCEPTION, e);
        }
    }

    private static ResponseEntity<CustomExceptionEntity> generalResponse(ExceptionType type, Exception e) {
        return new ResponseEntity<>(new CustomExceptionEntity(type, type.getMessage(), e), type.getStatus());
    }
}
