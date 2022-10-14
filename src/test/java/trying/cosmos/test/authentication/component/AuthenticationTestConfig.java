package trying.cosmos.test.authentication.component;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthenticationTestConfig {

    @Bean
    public AuthenticationTestController authenticationController() {
        return new AuthenticationTestController();
    }
}
