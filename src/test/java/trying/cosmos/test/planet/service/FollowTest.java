package trying.cosmos.test.planet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.repository.PlanetFollowRepository;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.PLANET_FOLLOW_FAILED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 행성 팔로우")
public class FollowTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    PlanetFollowRepository planetFollowRepository;

    private Long userId;
    private Long followerId;
    private Long planetId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, PlanetImageType.EARTH));
        this.planetId = planet.getId();
        User follower = userRepository.save(new User("follower@gmail.com", PASSWORD, "follow", LOGIN, USER));
        this.followerId = follower.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("팔로우 성공")
        void follow() throws Exception {
            planetService.follow(followerId, planetId);
            assertThat(planetFollowRepository.searchByUserAndPlanet(followerId, planetId)).isPresent();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 행성인 경우")
        void follow_my_planet() throws Exception {
            assertThatThrownBy(() -> planetService.follow(userId, planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PLANET_FOLLOW_FAILED.getMessage());
        }

        @Test
        @DisplayName("이미 팔로우 되어있는 경우")
        void followed() throws Exception {
            planetService.follow(followerId, planetId);
            assertThatThrownBy(() -> planetService.follow(followerId, planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PLANET_FOLLOW_FAILED.getMessage());
        }
    }
}
