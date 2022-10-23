package trying.cosmos.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_HEADER = "accessToken";
    private static final String SUBJECT_KEY = "sub";
    private static final String AUTHORITY_KEY = "auth";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        AuthorityOf annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthorityOf.class);
        // 권한이 필요하지 않는 함수인 경우 다음 인터셉터로
        if (annotation == null) {
            return true;
        }
        AuthKey.setNeed(true);

        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token == null || !tokenProvider.validateToken(token)) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        Map<String, String> userData = tokenProvider.parseToken(token);

        Authority authority = annotation.value();
        if (Authority.valueOf(userData.get(AUTHORITY_KEY)).level < authority.level) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        User user = userRepository.findByEmail(userData.get(SUBJECT_KEY)).orElseThrow(() -> new CustomException(ExceptionType.AUTHENTICATION_FAILED));

        if (!user.getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.NOT_AUTHENTICATED);
        }

        AuthKey.setKey(user.getId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthKey.remove();
    }
}
