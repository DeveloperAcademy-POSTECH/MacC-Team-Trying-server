package trying.cosmos.global.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* *..*Controller.*(..))")
    public void controllerObject() {}
}
