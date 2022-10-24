package trying.cosmos.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.auth.repository.SessionRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private static final String ACCESS_TOKEN_HEADER = "accessToken";

    private final SessionRepository sessionRepository;
    private final TokenProvider tokenProvider;

    public Session checkAuthenticate(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token == null || !tokenProvider.validateToken(token)) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        Optional<Session> optionalAuth = sessionRepository.findById(tokenProvider.getSubject(token));
        if (optionalAuth.isEmpty()) {
            throw new CustomException(ExceptionType.NOT_AUTHENTICATED);
        }
        Session auth = optionalAuth.get();

        if (!auth.getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.NOT_AUTHENTICATED);
        }
        return auth;
    }
}
