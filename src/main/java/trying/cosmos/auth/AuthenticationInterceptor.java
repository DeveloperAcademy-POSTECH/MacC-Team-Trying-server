package trying.cosmos.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_HEADER = "accessToken";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        AuthorityOf annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthorityOf.class);
        if (annotation == null) {
            return true;
        }

        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token == null || !tokenProvider.validateToken(token)) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        User user = userRepository.findByEmail(tokenProvider.getSubject(token))
                .orElseThrow(() -> new CustomException(ExceptionType.AUTHENTICATION_FAILED));

        if (!user.getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        Authority authority = annotation.value();
        if (user.getAuthority().level < authority.level) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        AuthKey.set(user.getId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthKey.remove();
    }
}
