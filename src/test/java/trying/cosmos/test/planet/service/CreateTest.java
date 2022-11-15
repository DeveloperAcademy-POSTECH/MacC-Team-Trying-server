package trying.cosmos.test.planet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("행성 생성")
public class CreateTest {

    @Autowired
    PlanetService planetService;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("행성이 존재한다면 PLANET_CREATION_FAILED 오류를 발생시킨다.")
        void planet_exist() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            planetService.create(user.getId(), NAME1, IMAGE);

            // WHEN THEN
            assertThatThrownBy(() -> planetService.create(user.getId(), NAME2, IMAGE))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.PLANET_CREATE_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>사용자에 행성 추가</li>
         *     <li>행성에 사용자 추가</li>
         * </ol>
         */
        @Test
        @DisplayName("행성을 생성한다.")
        void create() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));

            // WHEN
            Planet planet = planetService.create(user.getId(), NAME1, IMAGE);

            // THEN
            assertThat(user.getPlanet()).isEqualTo(planet);
            assertThat(planet.getOwners()).contains(user);
        }
    }
}
