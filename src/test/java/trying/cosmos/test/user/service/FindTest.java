package trying.cosmos.test.user.service;

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
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.planet.entity.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.entity.UserStatus.*;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.global.exception.ExceptionType.SUSPENDED_USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 사용자 조회")
public class FindTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetService planetService;

    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("행성이 없으면 hasPlanet()과 hasMate가 false")
        void not_has_planet() throws Exception {
            User user = userService.find(userId);
            assertThat(user.getPlanet()).isNull();
            assertThat(user.getMate()).isNull();
        }

        @Test
        @DisplayName("행성은 있고 메이트는 없으면 hasPlanet()는 true, hasMate는 false")
        void not_has_mate() throws Exception {
            User user = userService.find(userId);
            planetRepository.save(new Planet(user, PLANET_NAME, EARTH, generateCode()));
            assertThat(user.getPlanet()).isNotNull();
            assertThat(user.getMate()).isNull();
        }

        @Test
        @DisplayName("행성은 있고 메이트는 없으면 hasPlanet()는 true, hasMate는 false")
        void has_mate() throws Exception {
            User user = userService.find(userId);
            Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, EARTH, generateCode()));
            User mate = userRepository.save(new User("mate@gmail.com", PASSWORD, "mate"));
            planetService.join(mate.getId(), planet.getInviteCode());
            assertThat(user.getPlanet()).isNotNull();
            assertThat(user.getMate()).isNotNull();
        }
    }

    private String generateCode() {
        String code = RandomStringUtils.random(6, true, true);
        while (planetRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.random(6, true, true);
        }
        return code;
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("사용자의 상태가 SUSPENDED인 경우")
        void suspended_user() throws Exception {
            User user = userRepository.save(new User("suspended@gmail.com", PASSWORD, "suspended", SUSPENDED, USER));
            assertThatThrownBy(() -> userService.find(user.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(SUSPENDED_USER.getMessage());
        }

        @Test
        @DisplayName("사용자의 상태가 WITHDRAWN인 경우")
        void withdrawn_user() throws Exception {
            User user = userRepository.save(new User("withdrawn@gmail.com", PASSWORD, "withdrawn", WITHDRAWN, USER));
            assertThatThrownBy(() -> userService.find(user.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
    }
}
