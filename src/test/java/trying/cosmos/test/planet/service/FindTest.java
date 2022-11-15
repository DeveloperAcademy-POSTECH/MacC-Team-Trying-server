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

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("초대 코드를 이용해서 행성 조회")
public class FindTest {

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
            // WHEN THEN
            assertThatThrownBy(() -> planetService.find(WRONG_INVITE_CODE))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("행성에 빈 자리가 없으면 NO_DATA 오류를 발생시킨다.")
        void planet_is_full() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            // WHEN THEN
            assertThatThrownBy(() -> planetService.find(planet.getInviteCode()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("행성을 조회한다.")
        void find() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));

            // WHEN
            Planet findPlanet = planetService.find(planet.getInviteCode());

            // THEN
            assertThat(findPlanet).isEqualTo(planet);
        }
    }
}
