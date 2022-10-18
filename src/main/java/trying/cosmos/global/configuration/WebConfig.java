package trying.cosmos.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.auth.AnonymousInterceptor;
import trying.cosmos.global.auth.AuthenticationInterceptor;
import trying.cosmos.global.auth.TokenProvider;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(userRepository, tokenProvider));
        registry.addInterceptor(new AnonymousInterceptor(userRepository, tokenProvider));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}
