package trying.cosmos.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionType error;

    public CustomException(ExceptionType error) {
        super(error.getMessage());
        this.error = error;
    }
}