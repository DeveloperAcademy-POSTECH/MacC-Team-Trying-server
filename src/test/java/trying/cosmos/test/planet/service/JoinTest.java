package trying.cosmos.test.planet.service;

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
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.PLANET_JOIN_FAILED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 행성 참가")
public class JoinTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    private Long hostId;
    private Long guestId;
    private Long planetId;
    private String inviteCode;

    @BeforeEach
    void setup() {
        User host = userRepository.save(new User(EMAIL, PASSWORD, "host", LOGIN, USER));
        this.hostId = host.getId();
        User guest = userRepository.save(new User("guest@gmail.com", PASSWORD, "guest", LOGIN, USER));
        this.guestId = guest.getId();

        Planet planet = planetRepository.save(new Planet(host, PLANET_NAME, EARTH));
        this.planetId = planet.getId();
        this.inviteCode = planet.getInviteCode();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("행성 참가시 user.getMate()시 서로가 반환, user.getPlanet()시 행성이 반환, planet.isOwnedBy(user) = true")
        void join_mate() throws Exception {
            planetService.join(guestId, inviteCode);
            User host = userRepository.findById(hostId).orElseThrow();
            User guest = userRepository.findById(guestId).orElseThrow();
            Planet planet = planetRepository.findById(planetId).orElseThrow();

            assertThat(host.getMate()).isEqualTo(guest);
            assertThat(guest.getMate()).isEqualTo(host);
            assertThat(guest.getPlanet()).isEqualTo(planet);
            assertThat(planet.isOwnedBy(guest)).isTrue();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 행성인 경우")
        void my_planet() throws Exception {
            assertThatThrownBy(() -> planetService.join(hostId, inviteCode))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PLANET_JOIN_FAILED.getMessage());
        }

        @Test
        @DisplayName("행성에 메이트가 이미 있는 경우")
        void full_planet() throws Exception {
            planetService.join(guestId, inviteCode);
            User newUser = userRepository.save(new User("new@gmail.com", PASSWORD, "new", LOGIN, USER));
            assertThatThrownBy(() -> planetService.join(newUser.getId(), inviteCode))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PLANET_JOIN_FAILED.getMessage());
        }
    }
}
