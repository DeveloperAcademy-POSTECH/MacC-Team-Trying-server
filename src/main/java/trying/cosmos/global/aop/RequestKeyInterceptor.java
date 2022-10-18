package trying.cosmos.global.aop;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class RequestKeyInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<String> requestKey = new ThreadLocal<>();

    public static String getRequestKey() {
        return requestKey.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestKey.set(UUID.randomUUID().toString().substring(0, 8));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        requestKey.remove();
    }
}
