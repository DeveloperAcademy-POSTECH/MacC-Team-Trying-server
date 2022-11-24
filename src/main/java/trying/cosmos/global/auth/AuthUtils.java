package trying.cosmos.global.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.global.aop.LogSpace;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtils {

    private static final String ACCESS_TOKEN_HEADER = "accessToken";

    private final SessionService sessionService;
    private final TokenProvider tokenProvider;

    public Session checkAuthenticate(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        log.debug("{}token: {}", LogSpace.getSpace(), token);
        if (token == null) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }
        if (!tokenProvider.validateToken(token)) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        Session auth = sessionService.findById(tokenProvider.getSubject(token))
                .orElseThrow(() -> new CustomException(ExceptionType.NOT_AUTHENTICATED));

        if (!auth.getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.NOT_AUTHENTICATED);
        }
        return auth;
    }
}
