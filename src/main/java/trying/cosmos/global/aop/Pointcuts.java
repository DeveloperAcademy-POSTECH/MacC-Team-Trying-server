package trying.cosmos.global.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* *..*Controller.*(..))")
    public void controllerObject() {}

    @Pointcut("execution(* trying.cosmos.domain..*(..))")
    public void domainObject() {}

    @Pointcut("execution(* trying.cosmos.global.auth..*(..))")
    public void authObject() {}

    @Pointcut("execution(* trying.cosmos.global.dev..*(..))")
    public void devObject() {}

    @Pointcut("execution(* trying.cosmos.global.utils..*(..))")
    public void utilsObject() {}

    @Pointcut("domainObject() || authObject() || devObject() || utilsObject()")
    public void methodLogObject() {}
}
