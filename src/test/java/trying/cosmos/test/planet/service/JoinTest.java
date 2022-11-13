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

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.global.exception.ExceptionType.PLANET_JOIN_FAILED;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("행성 참가")
public class JoinTest {

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
        @DisplayName("행성이 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void no_planet() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // WHEN THEN
            assertThatThrownBy(() -> planetService.join(user.getId(), WRONG_INVITE_CODE))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("행성에 빈 자리가 없으면 NO_DATA 오류를 발생시킨다.")
        void planet_is_full() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            User guest = userRepository.save(User.createEmailUser(EMAIL3, PASSWORD, NAME3, DEVICE_TOKEN));

            // WHEN THEN
            assertThatThrownBy(() -> planetService.join(guest.getId(), INVITE_CODE))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
        
        @Test
        @DisplayName("내 행성이면 PLANET_JOIN_FAILED 오류를 발생시킨다.")
        void my_planet() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            
            // WHEN THEN
            assertThatThrownBy(() -> planetService.join(user.getId(), INVITE_CODE))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(PLANET_JOIN_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>행성에 사용자 추가</li>
         *     <li>사용자에 행성 지정</li>
         *     <li>메이트 지정</li>
         * </ol>
         */
        @Test
        @DisplayName("행성 참가")
        void join() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));

            // WHEN
            planetService.join(mate.getId(), INVITE_CODE);
            
            // THEN
            assertThat(planet.getOwners()).contains(user, mate);
            assertThat(mate.getPlanet()).isEqualTo(planet);
            assertThat(user.getMate()).isEqualTo(mate);
            assertThat(mate.getMate()).isEqualTo(user);
        }
    }
}
