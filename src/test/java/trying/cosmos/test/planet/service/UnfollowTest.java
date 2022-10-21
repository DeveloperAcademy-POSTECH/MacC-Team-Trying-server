package trying.cosmos.test.planet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.CourseRepository;
import trying.cosmos.domain.planet.*;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 행성 언팔로우")
public class UnfollowTest {

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
        planetService.follow(followerId, planetId);
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("팔로우 성공")
        void follow() throws Exception {
            planetService.unfollow(followerId, planetId);
            assertThat(planetFollowRepository.searchByUserAndPlanet(followerId, planetId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 행성인 경우")
        void follow_my_planet() throws Exception {
            assertThatThrownBy(() -> planetService.unfollow(userId, planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }

        @Test
        @DisplayName("팔로우 되어있지 않은 경우")
        void followed() throws Exception {
            planetService.unfollow(followerId, planetId);
            assertThatThrownBy(() -> planetService.unfollow(followerId, planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
    }
}