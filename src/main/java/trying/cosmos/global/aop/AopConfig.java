package trying.cosmos.global.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AopConfig {

    @Bean
    public LoggerAspect loggerAspect() {
        return new LoggerAspect();
    }
}
