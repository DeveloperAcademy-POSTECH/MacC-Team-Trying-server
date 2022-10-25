package trying.cosmos.test.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.entity.Access;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.exception.CustomException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 코스 수정")
public class DeleteTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;

    private Long userId;
    private Long courseId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, UserStatus.LOGIN, Authority.USER));
        this.userId = user.getId();
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, PlanetImageType.EARTH));
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));
        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagRequest, null);
        this.courseId = course.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 삭제")
        void update() throws Exception {
            courseService.delete(userId, courseId);
            assertThatThrownBy(() -> courseService.find(userId, courseId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 코스가 아닌 경우")
        void not_my_course() throws Exception {
            User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", UserStatus.LOGIN, Authority.USER));
            assertThatThrownBy(() -> courseService.delete(other.getId(), courseId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_PERMISSION.getMessage());
        }
    }
}
