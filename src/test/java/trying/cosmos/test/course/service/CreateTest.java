package trying.cosmos.test.course.service;

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
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.place.service.PlaceService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.course.entity.Access.PUBLIC;
import static trying.cosmos.domain.planet.entity.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 코스 생성")
public class CreateTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;

    @Autowired
    PlaceService placeService;

    private Long userId;
    private Long planetId;
    private List<TagCreateRequest> tagRequest;

    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();

        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, EARTH));
        this.planetId = planet.getId();

        tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 생성 성공")
        void create() throws Exception {
            courseService.create(userId, planetId, TITLE, BODY, PUBLIC, tagRequest, null);
            assertThat(planetService.findPlanetCourse(userId, planetId, pageable).getNumberOfElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 행성이 아닌 경우")
        void not_my_planet() throws Exception {
            User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", LOGIN, USER));
            assertThatThrownBy(() -> courseService.create(other.getId(), planetId, TITLE, BODY, PUBLIC, tagRequest, null))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_PERMISSION.getMessage());
        }
    }
}
