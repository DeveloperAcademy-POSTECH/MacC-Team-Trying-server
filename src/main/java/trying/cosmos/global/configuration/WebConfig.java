package trying.cosmos.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import trying.cosmos.global.aop.AuthenticationLogInterceptor;
import trying.cosmos.global.aop.RequestKeyInterceptor;
import trying.cosmos.global.aop.RequestLogInterceptor;
import trying.cosmos.global.auth.AuthUtils;
import trying.cosmos.global.auth.interceptor.AnonymousInterceptor;
import trying.cosmos.global.auth.interceptor.AuthenticationInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthUtils authUtils;

    @Value("${cloud.aws.cloudfront.url}")
    private String host;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestKeyInterceptor());
        registry.addInterceptor(new RequestLogInterceptor());
        registry.addInterceptor(new AuthenticationInterceptor(authUtils));
        registry.addInterceptor(new AnonymousInterceptor(authUtils));
        registry.addInterceptor(new AuthenticationLogInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/images/**").addResourceLocations(host + "/");
    }
}
