package trying.cosmos.test.planet.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Repository) 행성이 존재하지 않는 경우")
public class NotExistTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("id에 해당하는 행성이 존재하지 않는 경우")
        void id() throws Exception {
            assertThat(planetRepository.findById(-1L)).isEmpty();
        }

        @Test
        @DisplayName("초대 코드에 해당하는 행성이 존재하지 않는 경우")
        void invite_code() throws Exception {
            assertThat(planetRepository.searchByInviteCode("wrong")).isEmpty();
        }
    }
}
