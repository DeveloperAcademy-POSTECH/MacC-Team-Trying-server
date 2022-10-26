package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static trying.cosmos.global.auth.entity.AuthKey.*;

@Slf4j
public class AuthenticationLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("[Authentication Info]");
        log.debug("");
        getAuthenticationInfoLog();
        log.debug("");
        log.debug("[RunTime Info]");
        log.debug("");
        return true;
    }

    private static void getAuthenticationInfoLog() {
        log.debug("- Need Authenticate: {}", needAuthenticate());
        log.debug("- Is Authenticated: {}", isAuthenticated());
        if (isAuthenticated()) {
            log.debug("- Authentication Key: {}", getKey());
        }
    }
}
