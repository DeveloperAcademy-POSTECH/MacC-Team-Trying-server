package trying.cosmos.global.utils.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.global.auth.Authority;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final PlanetService planetService;

    private static final String MOCK_DEVICE_TOKEN = "deviceToken";

    @Transactional
    public User createUser() {
        String str = randomName();
        return userRepository.save(new User(str + "@gmail.com", "password", str, UserStatus.LOGIN, Authority.USER));
    }

    @Transactional
    public Planet createPlanet(User user) {
        return planetRepository.save(new Planet(user, randomName(), PlanetImageType.EARTH));
    }

    private String randomName() {
        return random(6, true, true);
    }
}
