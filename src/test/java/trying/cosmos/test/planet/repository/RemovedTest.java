package trying.cosmos.test.planet.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Repository) 삭제 테스트")
public class RemovedTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetService planetService;

    private Planet planet;
    private Planet deleted;
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.planet = planetRepository.save(new Planet(user, "행성", PlanetImageType.EARTH));
        this.deleted = planetRepository.save(new Planet(user, "삭제된 행성", PlanetImageType.EARTH));
        planetService.delete(user.getId(), deleted.getId());
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("이름으로 행성 조회시 삭제된 행성은 제외")
        void search_name_deleted() throws Exception {
            assertThat(planetRepository.searchByNameLike("%행성%", pageable)).containsExactly(planet);
        }
    }
    
    @Nested
    @DisplayName("실패")
    class fail {
        
        @Test
        @DisplayName("id로 행성 조회시 삭제된 행성인 경우")
        void search_id_deleted() throws Exception {
            assertThat(planetRepository.searchById(deleted.getId())).isEmpty();
        }

        @Test
        @DisplayName("초대 코드로 행성 조회시 삭제된 행성인 경우")
        void search_code_deleted() throws Exception {
            assertThat(planetRepository.searchByInviteCode(deleted.getInviteCode())).isEmpty();
        }
    }
}
