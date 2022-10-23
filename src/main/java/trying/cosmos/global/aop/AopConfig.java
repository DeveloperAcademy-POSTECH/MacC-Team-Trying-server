package trying.cosmos.global.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AopConfig {

    @Bean
    public RequestLogInterceptor requestLogger() {
        return new RequestLogInterceptor();
    }

    @Bean
    public MethodLoggerAspect methodLogger() {
        return new MethodLoggerAspect();
    }
}
