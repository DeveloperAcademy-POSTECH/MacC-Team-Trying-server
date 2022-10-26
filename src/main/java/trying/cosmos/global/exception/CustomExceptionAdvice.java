package trying.cosmos.global.exception;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.NoSuchElementException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionEntity> custom(CustomException e) {
        return new ResponseEntity<>(new CustomExceptionEntity(e.getError(), e.getMessage(), e), e.getError().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomExceptionEntity> validation(MethodArgumentNotValidException e) {
        return generalResponse(ExceptionType.INVALID_INPUT, getBindingMessage(e), e);
    }

    private String getBindingMessage(MethodArgumentNotValidException e) {
        StringBuilder builder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error ->
                builder.append(error.getDefaultMessage()).append(" ")
        );
        return builder.substring(0, builder.length() - 1);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CustomExceptionEntity> duplicated(MaxUploadSizeExceededException e) {
        return generalResponse(ExceptionType.INPUT_SIZE_EXCEEDED, e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomExceptionEntity> input(Exception e) {
        if (e.getMessage().contains("Enum")) {
            return generalResponse(ExceptionType.NO_DATA, e);
        } else if (e.getMessage().contains("NumberFormat")) {
            return generalResponse(ExceptionType.INVALID_TYPE, e);
        } else if (e.getMessage().contains("UnrecognizedPropertyException")) {
            return generalResponse(ExceptionType.INVALID_INPUT, e);
        } else if (e.getMessage().contains("JSON")) {
            return generalResponse(ExceptionType.INVALID_JSON_FORMAT, e);
        } else {
            return generalResponse(ExceptionType.UNKNOWN_EXCEPTION, e);
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomExceptionEntity> type(MethodArgumentTypeMismatchException e) {
        return generalResponse(ExceptionType.INVALID_TYPE, e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomExceptionEntity> parameter(MissingServletRequestParameterException e) {
        return generalResponse(ExceptionType.INVALID_PARAMETER, e);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<CustomExceptionEntity> parameter(MissingServletRequestPartException e) {
        return generalResponse(ExceptionType.INVALID_PARAMETER, e);
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MultipartException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<CustomExceptionEntity> header(Exception e) {
        return generalResponse(ExceptionType.INVALID_HEADER, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomExceptionEntity> method(HttpRequestMethodNotSupportedException e) {
        return generalResponse(ExceptionType.NO_DATA, e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<CustomExceptionEntity> nodata(NoSuchElementException e) {
        return generalResponse(ExceptionType.NO_DATA, e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomExceptionEntity> duplicated(DataIntegrityViolationException e) {
        return generalResponse(ExceptionType.DUPLICATED, e);
    }

    @RestControllerAdvice
    private static class UnknownExceptionAdvice {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<CustomExceptionEntity> global(Exception e) {
            return generalResponse(ExceptionType.UNKNOWN_EXCEPTION, e);
        }
    }

    private static ResponseEntity<CustomExceptionEntity> generalResponse(ExceptionType type, Exception e) {
        return new ResponseEntity<>(new CustomExceptionEntity(type, type.getMessage(), e), type.getStatus());
    }

    private static ResponseEntity<CustomExceptionEntity> generalResponse(ExceptionType type, String message, Exception e) {
        return new ResponseEntity<>(new CustomExceptionEntity(type, message, e), type.getStatus());
    }
}
