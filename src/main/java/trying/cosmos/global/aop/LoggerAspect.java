package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

import static trying.cosmos.global.auth.AuthKey.*;

@Slf4j
@Aspect
public class LoggerAspect {

    @Autowired(required = false) private HttpServletRequest request;

    private static final int LINE_WIDTH = 50;

    @Around("trying.cosmos.global.aop.Pointcuts.controllerObject()")
    public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
        String request = joinPoint.getSignature().toShortString();
        int length = LINE_WIDTH - request.length();
        log.debug("");
        log.debug("{} [{}] {} {}", getLine(length / 2), RequestKeyInterceptor.getRequestKey(), request, getLine((length + 1) / 2));
        log.debug("");
        log.debug("[General Info]");
        log.debug("- URL: {}", this.request.getRequestURL());
        log.debug("- Method: {}", this.request.getMethod());
        log.debug("");
        log.debug("[Authentication Info]");
        log.debug("- Need Authenticate: {}", needAuthenticate());
        log.debug("- Is Authenticated: {}", isAuthenticated());
        if (needAuthenticate() && isAuthenticated()) {
            log.debug("- Authentication Key: {}", getKey());
        }
        log.debug("");
        log.debug("[RunTime Info]");
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.debug("- Result: Success in {}ms", endTime - startTime);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.debug("- Result: Failed in {}ms", endTime - startTime);
            throw e;
        }
    }

    private String getLine(int length) {
        return "=".repeat(length);
    }
}
