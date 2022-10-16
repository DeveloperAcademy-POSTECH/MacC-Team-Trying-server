package trying.cosmos.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.PlanetRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.PlanetService;

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
        return userRepository.save(new User(str + "@gmail.com", "password", str, UserStatus.LOGOUT, Authority.USER));
    }

    @Transactional
    public Planet createPlanet() {
        return planetRepository.save(new Planet(createUser(), randomName(), PlanetImageType.EARTH));
    }

    private String randomName() {
        return random(6, true, true);
    }
}
