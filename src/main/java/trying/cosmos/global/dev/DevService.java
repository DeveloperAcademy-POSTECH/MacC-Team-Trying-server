package trying.cosmos.global.dev;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
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
        User user = User.createEmailUser(str + "@gmail.com", "password", str, "device_token");
        return userRepository.save(user);
    }

    @Transactional
    public Planet createPlanet(User user) {
        return planetService.create(user.getId(), randomName(), "EARTH");
    }

    @Transactional
    public Planet createMate(User user, User mate) {
        Planet planet = planetService.create(user.getId(), randomName(), "EARTH");
        planetService.join(mate.getId(), planet.getInviteCode());
        return planet;
    }

    private String randomName() {
        return RandomStringUtils.random(6, true, true);
    }
}
