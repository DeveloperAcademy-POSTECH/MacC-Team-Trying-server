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
    public void createUser(String email, String password, String name) {
        userRepository.save(new User(email, password, name, UserStatus.LOGOUT, Authority.USER));
    }

    @Transactional
    public Planet createPlanet(String email, String password, String name, String planetName, PlanetImageType type) {
        User user = userRepository.save(new User(email, password, name, UserStatus.LOGOUT, Authority.USER));
        User mate = userRepository.save(new User(random(5), random(5), random(5), UserStatus.LOGOUT, Authority.USER));
        Planet planet = planetRepository.save(new Planet(user, planetName, type));
        planetService.joinPlanet(mate.getId(), planetService.getInviteCode(user.getId(), planet.getId()));
        return planet;
    }
}
