package trying.cosmos.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.aop.AuthenticationLogInterceptor;
import trying.cosmos.global.aop.RequestKeyInterceptor;
import trying.cosmos.global.aop.RequestLogInterceptor;
import trying.cosmos.global.auth.AnonymousInterceptor;
import trying.cosmos.global.auth.AuthenticationInterceptor;
import trying.cosmos.global.auth.TokenProvider;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Value("${cloud.aws.cloudfront.url}")
    private String host;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestKeyInterceptor());
        registry.addInterceptor(new RequestLogInterceptor());
        registry.addInterceptor(new AuthenticationInterceptor(userRepository, tokenProvider));
        registry.addInterceptor(new AnonymousInterceptor(userRepository, tokenProvider));
        registry.addInterceptor(new AuthenticationLogInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/image/**").addResourceLocations(host + "/");
    }
}
