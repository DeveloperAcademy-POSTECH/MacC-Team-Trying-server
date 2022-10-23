package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static trying.cosmos.global.auth.AuthKey.*;

@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final int LINE_WIDTH = 50;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        String signature = getSignature(handlerMethod.getMethod());
        int length = LINE_WIDTH - signature.length();
        log.debug("");
        getSignatureLog(signature, length);
        log.debug("");
        getGeneralInfoLog(request);
        return true;
    }

    private String getLine(int length) {
        return "=".repeat(length);
    }

    private String getSignature(Method method) {
        String className = method.getDeclaringClass().toString();
        int idx = className.lastIndexOf(".");
        return className.substring(idx + 1) + "." + method.getName();
    }

    private void getSignatureLog(String signature, int length) {
        log.debug("{} [{}] {} {}", getLine(length / 2), RequestKeyInterceptor.getRequestKey(), signature, getLine((length + 1) / 2));
    }

    private void getGeneralInfoLog(HttpServletRequest request) {
        log.debug("[General Info]");
        log.debug("- URL: {}", request.getRequestURL());
        log.debug("- Method: {}", request.getMethod());
    }

    private static void getAuthenticationInfoLog() {
        log.debug("[Authentication Info]");
        log.debug("- Need Authenticate: {}", needAuthenticate());
        log.debug("- Is Authenticated: {}", isAuthenticated());
        if (needAuthenticate() && isAuthenticated()) {
            log.debug("- Authentication Key: {}", getKey());
        }
    }
}
