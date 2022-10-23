package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static trying.cosmos.global.auth.AuthKey.*;

@Slf4j
public class AuthenticationLogInterceptor implements HandlerInterceptor {

    private static final int LINE_WIDTH = 50;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String signature = request.getMethod();
        log.debug("");
        getAuthenticationInfoLog();
        log.debug("");
        log.debug("[RunTime Info]");
        return true;
    }

//    public Object requestInfoLogger(ProceedingJoinPoint joinPoint) throws Throwable {
//        String signature = getSignature(joinPoint);
//        int length = LINE_WIDTH - signature.length();
//        log.debug("");
//        getSignatureLog(signature, length);
//        log.debug("");
//        getGeneralInfoLog();
//        log.debug("");
//        getAuthenticationInfoLog();
//        log.debug("");
//        log.debug("[RunTime Info]");
//        return joinPoint.proceed();
//    }

    private String getLine(int length) {
        return "=".repeat(length);
    }

    private String getSignature(ProceedingJoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toShortString();
        int idx = signature.indexOf("(");
        return signature.substring(0, idx);
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
