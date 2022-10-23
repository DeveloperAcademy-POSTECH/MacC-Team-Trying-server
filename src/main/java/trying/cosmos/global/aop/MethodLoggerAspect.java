package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Slf4j
@Aspect
public class MethodLoggerAspect {

    @Around("trying.cosmos.global.aop.Pointcuts.methodLogObject()")
    public Object requestInfoLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = getSignature(joinPoint);

        log.debug("");
        log.debug("{}[{} Start]", LogSpace.getSpace(), signature);
        if (joinPoint.getArgs().length != 0) {
            log.debug("{}(Parameters)", LogSpace.getSpace());
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        for (int i = 0; i < method.getParameters().length; i++) {
            log.debug("{}- {}: {}", LogSpace.getSpace(), method.getParameters()[i].getName(), joinPoint.getArgs()[i].toString());
        }
        long startTime = System.currentTimeMillis();
        try {
            LogSpace.add();
            Object result = joinPoint.proceed();
            LogSpace.sub();
            long endTime = System.currentTimeMillis();

            log.debug("");
            log.debug("{}[{} Success in {}ms]", LogSpace.getSpace(), signature, endTime - startTime);
            if (result != null) {
                log.debug("{}(Result)", LogSpace.getSpace());
                log.debug("{}- {}", LogSpace.getSpace(), result);
            }
            return result;
        } catch (Exception e) {
            LogSpace.sub();
            long endTime = System.currentTimeMillis();
            log.debug("");
            log.debug("{}[{} Failed in {}ms]", LogSpace.getSpace(), signature, endTime - startTime);
            throw e;
        } finally {
            if (LogSpace.needRemove()) {
                LogSpace.remove();
            }
        }
    }

    private String getSignature(ProceedingJoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toShortString();
        return signature.substring(0, signature.indexOf("("));
    }
}
