package trying.cosmos.global.auth.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.global.auth.AuthUtils;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthUtils authUtils;
    private static final String AUTHORITY_KEY = "auth";

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

        Session auth = authUtils.checkAuthenticate(request);
        Authority authority = annotation.value();
        if (auth.getAuthority().level < authority.level) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        AuthKey.setKey(auth.getUserId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthKey.remove();
    }
}
