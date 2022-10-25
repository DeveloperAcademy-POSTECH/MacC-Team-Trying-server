package trying.cosmos.test.planet.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.planet.entity.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 행성 수정")
public class UpdateTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetService planetService;

    private Long userId;
    private Long planetId;
    private Long guestId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
        User guest = userRepository.save(new User("guest@gmail.com", PASSWORD, "guest", LOGIN, USER));
        this.guestId = guest.getId();
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, EARTH, generateCode()));
        this.planetId = planet.getId();
    }

    private String generateCode() {
        String code = RandomStringUtils.random(6, true, true);
        while (planetRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.random(6, true, true);
        }
        return code;
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("수정")
        void update_dday() throws Exception {
            planetService.update(userId, planetId, "updated", 10);
            Planet planet = planetRepository.findById(planetId).orElseThrow();
            assertThat(planet.getName()).isEqualTo("updated");
            assertThat(planet.getDday()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("유저의 행성이 아닌 경우")
        void dday_not_positive() throws Exception {
            assertThatThrownBy(() -> planetService.update(guestId, planetId, "updated", 10))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_PERMISSION.getMessage());
        }
    }
}
