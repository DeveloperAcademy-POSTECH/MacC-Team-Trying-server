package trying.cosmos.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import trying.cosmos.global.aop.RequestKeyInterceptor;

@Slf4j
@Getter
public class CustomExceptionEntity {

    private final String code;
    private final String message;

    public CustomExceptionEntity(ExceptionType eType, String message, Exception e) {
        this.code = eType.toString();
        this.message = message;
        log.info("[{}] Exception {}, {}", RequestKeyInterceptor.getRequestKey(), code, message, e);
    }
}
