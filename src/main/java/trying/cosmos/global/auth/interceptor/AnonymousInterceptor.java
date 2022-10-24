package trying.cosmos.global.auth.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.global.auth.AuthUtils;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class AnonymousInterceptor implements HandlerInterceptor {

    private final AuthUtils authUtils;

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

        try {
            Session auth = authUtils.checkAuthenticate(request);
            AuthKey.setKey(auth.getUserId());
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
