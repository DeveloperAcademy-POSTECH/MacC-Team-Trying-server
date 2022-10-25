package trying.cosmos.global.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.entity.Authority;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PlanetRepository planetRepository;
    private final PlanetService planetService;
    private final Environment environment;

    @PostConstruct
    public void clearSession() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            sessionService.clear();
        }
    }

    @Transactional
    public User createUser() {
        String str = randomName();
        User user = new User(str + "@gmail.com", "password", str, UserStatus.LOGIN, Authority.USER);
        return userRepository.save(user);
    }

    @Transactional
    public Planet createPlanet(User user) {
        return planetService.create(user.getId(), randomName(), "EARTH");
    }

    private String randomName() {
        return random(6, true, true);
    }
}
