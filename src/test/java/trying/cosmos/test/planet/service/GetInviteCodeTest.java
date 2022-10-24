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
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 초대코드 확인")
public class GetInviteCodeTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    private Long userId;
    private Long planetId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();

        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, EARTH));
        this.planetId = planet.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("초대코드 조회 성공")
        void get_code() throws Exception {
            String inviteCode = planetService.getInviteCode(userId, planetId);
            assertThat(planetRepository.findById(planetId).orElseThrow().getInviteCode()).isEqualTo(inviteCode);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("행성의 주인이 아닌 경우 권한 없음")
        void not_owner() throws Exception {
            User guest = userRepository.save(new User("guest@gmail.com", PASSWORD, "guest", LOGIN, USER));
            assertThatThrownBy(() -> planetService.getInviteCode(guest.getId(), planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_PERMISSION.getMessage());
        }

        @Test
        @DisplayName("행성에 guest가 이미 초대된 경우")
        void planet_is_full() throws Exception {
            String inviteCode = planetService.getInviteCode(userId, planetId);
            User guest = userRepository.save(new User("guest@gmail.com", PASSWORD, "guest", LOGIN, USER));
            planetService.join(guest.getId(), inviteCode);
            assertThatThrownBy(() -> planetService.getInviteCode(userId, planetId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
    }
}
