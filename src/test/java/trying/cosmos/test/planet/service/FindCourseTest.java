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
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Planet.Service) 행성 리스트 조회")
public class FindCourseTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    private Planet myPlanet;
    private Planet otherPlanet;
    private Long userId;

    private Course myPublic;
    private Course myPrivate;
    private Course otherPublic;
    private Course otherPrivate;

    Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
        User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", LOGIN, USER));

        myPlanet = planetRepository.save(new Planet(user, PLANET_NAME, PLANET_IMAGE, generateCode()));
        otherPlanet = planetRepository.save(new Planet(other, PLANET_NAME, PLANET_IMAGE, generateCode()));

        myPublic = new Course(myPlanet, "my_public", "my_public", Access.PUBLIC);
        courseRepository.save(myPublic);
        myPrivate = new Course(myPlanet, "my_private", "my_private", Access.PRIVATE);
        courseRepository.save(myPrivate);
        otherPublic = new Course(otherPlanet, "other_public", "my_public", Access.PUBLIC);
        courseRepository.save(otherPublic);
        otherPrivate = new Course(otherPlanet, "other_private", "other_private", Access.PRIVATE);
        courseRepository.save(otherPrivate);
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
        @DisplayName("내 행성 코스 조회 시 모든 코스 반환")
        void my_planet() throws Exception {
            assertThat(planetService.findPlanetCourse(userId, myPlanet.getId(), pageable)).containsExactly(myPublic, myPrivate);
        }

        @Test
        @DisplayName("다른 사람 행성 코스 조회 시 공개 코스만 반환")
        void other_planet() throws Exception {
            assertThat(planetService.findPlanetCourse(userId, otherPlanet.getId(), pageable)).containsExactly(otherPublic);
        }

        @Test
        @DisplayName("userId가 null이면 공개 코스만 반환")
        void no_auth() throws Exception {
            assertThat(planetService.findPlanetCourse(null, otherPlanet.getId(), pageable)).containsExactly(otherPublic);
        }
    }
}
