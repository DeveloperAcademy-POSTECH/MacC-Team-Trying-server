package trying.cosmos.test.planet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 팔로우한 행성 조회")
public class GetFollowPlanetsTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    private Planet myPlanet;
    private Planet followPlanet;
    private Long userId;

    Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
        User follow = userRepository.save(new User("follow@gmail.com", PASSWORD, "follow", LOGIN, USER));
        myPlanet = planetRepository.save(new Planet(user, "not follow", PlanetImageType.EARTH));
        followPlanet = planetRepository.save(new Planet(follow, "follow", PlanetImageType.EARTH));
        planetService.follow(userId, followPlanet.getId());
    }

    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("팔로우한 행성 조회")
        void find_all() throws Exception {
            Slice<Planet> list = planetService.findFollowPlanets(userId, pageable);
            assertThat(list.getContent()).containsExactly(followPlanet);
        }
    }
}
