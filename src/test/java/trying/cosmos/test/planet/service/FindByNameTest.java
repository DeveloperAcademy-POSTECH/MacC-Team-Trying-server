package trying.cosmos.test.planet.service;

import org.apache.commons.lang3.RandomStringUtils;
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
import trying.cosmos.domain.planet.dto.response.PlanetListFindContent;
import trying.cosmos.domain.planet.dto.response.PlanetListFindResponse;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 이름으로 행성 조회")
public class FindByNameTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    private Planet planetTrue;
    private Planet planetFalse;

    Pageable pageable = PageRequest.of(0, 5);

    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
        planetTrue = planetRepository.save(new Planet(user, "search_true", PLANET_IMAGE, generateCode()));
        planetFalse = planetRepository.save(new Planet(user, "search_false", PLANET_IMAGE, generateCode()));
    }

    private String generateCode() {
        String code = RandomStringUtils.random(6, true, true);
        while (planetRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.random(6, true, true);
        }
        return code;
    }

    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("query가 빈 문자열인 경우 모든 행성 조회")
        void find_all() throws Exception {
            PlanetListFindResponse response = planetService.findList(userId, "", pageable);
            assertThat(response.getPlanets().stream().map(PlanetListFindContent::getName).collect(Collectors.toList())).containsExactly("search_true", "search_false");
        }

        @Test
        @DisplayName("query에 맞는 행성 조회")
        void find_true() throws Exception {
            PlanetListFindResponse response = planetService.findList(userId, "true", pageable);
            assertThat(response.getPlanets().stream().map(PlanetListFindContent::getName).collect(Collectors.toList())).containsExactly("search_true");
        }
    }
}
