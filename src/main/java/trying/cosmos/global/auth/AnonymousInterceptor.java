package trying.cosmos.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
public class AnonymousInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_HEADER = "accessToken";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        AuthorityOf annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthorityOf.class);
        // 권한이 필요한 함수인 경우 다음 인터셉터로
        if (annotation != null) {
            return true;
        }
        AuthKey.setNeed(false);

        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token == null || !tokenProvider.validateToken(token)) {
            return true;
        }

        Optional<User> optionalUser = userRepository.findByEmail(tokenProvider.getSubject(token));
        if (optionalUser.isEmpty()) {
            return true;
        }
        User user = optionalUser.get();

        if (!user.getStatus().equals(UserStatus.LOGIN)) {
            return true;
        }

        AuthKey.setKey(user.getId());
        return true;
    }
}
