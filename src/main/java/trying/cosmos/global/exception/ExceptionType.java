package trying.cosmos.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    INVALID_INPUT(BAD_REQUEST, "잘못된 입력값입니다."),
    INVALID_TYPE(BAD_REQUEST, "입력값의 타입이 맞지 않습니다."),
    INVALID_JSON_FORMAT(BAD_REQUEST, "입력값이 JSON 형식이 아닙니다."),
    INVALID_PARAMETER(BAD_REQUEST, "잘못된 파라미터가 입력되었습니다."),
    INVALID_HEADER(BAD_REQUEST, "필수 헤더가 입력되지 않았습니다."),
    INVALID_METHOD(BAD_REQUEST, "지원하지 않는 HTTP 메서드입니다."),
    INPUT_SIZE_EXCEEDED(BAD_REQUEST, "최대 입력 크기를 초과했습니다."),

    AUTHENTICATION_FAILED(UNAUTHORIZED, "인증에 실패했습니다."),
    NOT_AUTHENTICATED(UNAUTHORIZED, "로그인 상태가 아닙니다."),
    CERTIFICATION_FAILED(UNAUTHORIZED, "이메일 인증에 실패했습니다."),
    INVALID_PASSWORD(UNAUTHORIZED, "잘못된 비밀번호입니다."),

    NO_PERMISSION(FORBIDDEN, "권한이 없습니다."),
    SUSPENDED_USER(FORBIDDEN, "정지된 사용자입니다."),

    NO_DATA(NOT_FOUND, "데이터가 존재하지 않습니다."),

    DUPLICATED(CONFLICT, "중복된 데이터가 존재합니다."),
    EMAIL_DUPLICATED(CONFLICT, "중복된 이메일이 존재합니다."),
    NAME_DUPLICATED(CONFLICT, "중복된 닉네임이 존재합니다."),
    PLANET_CREATE_FAILED(CONFLICT, "참여한 행성이 존재합니다."),
    PLANET_JOIN_FAILED(CONFLICT, "행성 참여에 실패했습니다."),

    UNKNOWN_EXCEPTION(INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}

