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

        if (!LogSpace.isRoot()) {
            log.trace("");
        }
        log.debug("{}[{}]", LogSpace.getSpace(), signature);
        if (joinPoint.getArgs().length != 0) {
            log.trace("{}(Parameters)", LogSpace.getSpace());
        }
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        for (int i = 0; i < method.getParameters().length; i++) {
            log.trace("{}- {}: {}", LogSpace.getSpace(), method.getParameters()[i].getName(), joinPoint.getArgs()[i].toString());
        }

        long startTime = System.currentTimeMillis();
        try {
            LogSpace.add();
            Object result = joinPoint.proceed();
            LogSpace.sub();
            long endTime = System.currentTimeMillis();

            log.trace("");
            log.debug("{}[{} Success in {}ms]", LogSpace.getSpace(), signature, endTime - startTime);
            if (result != null) {
                log.trace("{}(Result)", LogSpace.getSpace());
                log.trace("{}- {}", LogSpace.getSpace(), result);
            }
            return result;
        } catch (Exception e) {
            LogSpace.sub();
            long endTime = System.currentTimeMillis();
            log.debug("{}[{} Failed in {}ms]", LogSpace.getSpace(), signature, endTime - startTime);
            throw e;
        } finally {
            if (LogSpace.isRoot()) {
                LogSpace.remove();
                log.trace("");
            }
        }
    }

    private String getSignature(ProceedingJoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toShortString();
        return signature.substring(0, signature.indexOf("("));
    }
}
