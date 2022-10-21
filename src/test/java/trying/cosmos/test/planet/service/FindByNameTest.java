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
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 이름으로 행성 조회")
public class FindByNameTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    private Planet planetTrue;
    private Planet planetFalse;

    Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        planetTrue = planetRepository.save(new Planet(user, "search_true", PlanetImageType.EARTH));
        planetFalse = planetRepository.save(new Planet(user, "search_false", PlanetImageType.EARTH));
    }

    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("query가 빈 문자열인 경우 모든 행성 조회")
        void find_all() throws Exception {
            Slice<Planet> list = planetService.findList("", pageable);
            assertThat(list.getContent()).containsExactly(planetTrue, planetFalse);
        }

        @Test
        @DisplayName("query에 맞는 행성 조회")
        void find_true() throws Exception {
            Slice<Planet> list = planetService.findList("true", pageable);
            assertThat(list.getContent()).containsExactly(planetTrue);
        }
    }
}
