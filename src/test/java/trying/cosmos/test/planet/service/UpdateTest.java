package trying.cosmos.test.planet.service;

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
import trying.cosmos.global.exception.ExceptionType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("행성 수정")
public class UpdateTest {

    @Autowired
    PlanetService planetService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Nested
    @DisplayName("실패")
    class fail {
        
        @Test
        @DisplayName("행성이 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_planet() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // THEN WHEN
            assertThatThrownBy(() -> planetService.update(user.getId(), "UPDATED", LocalDate.now(), IMAGE))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("행성 정보를 수정한다.")
        void update() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));

            // WHEN
            planetService.update(user.getId(), "UPDATED", LocalDate.now().minusDays(3), "UPDATED");
            
            // THEN
            assertThat(planet.getName()).isEqualTo("UPDATED");
            assertThat(planet.getDday()).isEqualTo(4);
            assertThat(planet.getImage()).isEqualTo("UPDATED");
        }
    }
}
