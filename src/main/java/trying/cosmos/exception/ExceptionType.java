package trying.cosmos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    AUTHENTICATION_FAILED(UNAUTHORIZED, "401-1", "로그인되지 않았습니다."),
    NO_CERTIFICATED(UNAUTHORIZED, "401-2", "이메일 인증이 완료되지 않았습니다."),
    INVALID_PASSWORD(UNAUTHORIZED, "401-3", "잘못된 비밀번호입니다."),

    NO_PERMISSION(FORBIDDEN, "403-1", "권한이 없습니다."),
    SUSPENDED(FORBIDDEN, "403-2", "정지된 사용자입니다."),

    NO_DATA(NOT_FOUND, "404-1", "데이터가 존재하지 않습니다."),

    DUPLICATED(CONFLICT, "409-1", "중복된 데이터가 존재합니다."),
    INVALID_TOKEN(NOT_FOUND, "409-2", "잘못된 토큰입니다"),

    UNKNOWN_EXCEPTION(INTERNAL_SERVER_ERROR, "500", "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

