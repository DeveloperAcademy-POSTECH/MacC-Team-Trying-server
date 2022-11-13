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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("행성 나가기")
public class LeaveTest {

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

            // WHEN THEN
            assertThatThrownBy(() -> planetService.leave(user.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>행성에서 사용자 제거</li>
         *     <li>사용자에서 행성 제거</li>
         *     <li>메이트 제거</li>
         * </ol>
         */
        @Test
        @DisplayName("행성을 나간 후 빈 행성이 아니라면 유지한다.")
        void leave() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            // WHEN
            planetService.leave(mate.getId());
            
            // THEN
            assertThat(mate.getPlanet()).isNull();
            assertThat(user.getMate()).isNull();
            assertThat(mate.getMate()).isNull();
            assertThat(planetRepository.searchById(planet.getId())).isPresent();
            assertThat(planet.getOwners()).doesNotContain(mate);
        }

        /**
         * <ol>
         *     <li>행성에서 사용자 제거</li>
         *     <li>사용자에서 행성 제거</li>
         *     <li>메이트 제거</li>
         * </ol>
         */
        @Test
        @DisplayName("행성을 나간 후 빈 행성이라면 행성을 삭제한다.")
        void leave_empty() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));

            // WHEN
            planetService.leave(user.getId());

            // THEN
            assertThat(planetRepository.searchById(planet.getId())).isEmpty();
        }
    }
}
