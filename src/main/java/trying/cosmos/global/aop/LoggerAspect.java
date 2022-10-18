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

    @Around("trying.cosmos.global.aop.Pointcuts.controllerObject()")
    public Object logger(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("");
        log.debug("General Info ===========================================================");
        log.debug("");
        log.debug("Request Key: {}", RequestKeyInterceptor.getRequestKey());
        log.debug("API: {}", joinPoint.getSignature().toShortString());
        log.debug("URL: {}", request.getRequestURL());
        log.debug("");
        log.debug("Authentication Info ====================================================");
        log.debug("");
        log.debug("Need Authenticate: {}", needAuthenticate());
        if (needAuthenticate()) {
            log.debug("Is Authenticated: {}", isAuthenticated());
        }
        if (needAuthenticate() && isAuthenticated()) {
            log.debug("Key: {}", getKey());
        }
        log.debug("");
        log.debug("RunTime Info ===========================================================");
        log.debug("");
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.debug("Result: Success in {}ms", endTime - startTime);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.debug("Result: Failed in {}ms", endTime - startTime);
            throw e;
        }
    }
}
