package trying.cosmos.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

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
        getSignatureLog(signature, length);
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
        log.debug("");
        log.debug("[General Info]");
        log.debug("- URL: {}", request.getRequestURL());
        log.debug("- Method: {}", request.getMethod());
        log.debug("");
    }
}
