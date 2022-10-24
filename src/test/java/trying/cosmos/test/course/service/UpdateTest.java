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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 코스 수정")
public class UpdateTest {

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
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));
        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagRequest, null);
        this.courseId = course.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 수정")
        void update() throws Exception {
            List<TagCreateRequest> updateTag = List.of(
                    new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME),
                    new TagCreateRequest(new PlaceCreateRequest(2L, "new place", 1.0, 1.0), "new tag")
            );
            courseService.update(userId, courseId, "updated", "updated", Access.PRIVATE, updateTag, null);
            assertThat(courseRepository.findById(courseId).orElseThrow().getTitle()).isEqualTo("updated");
            assertThat(courseRepository.findById(courseId).orElseThrow().getBody()).isEqualTo("updated");
            assertThat(courseRepository.findById(courseId).orElseThrow().getAccess()).isEqualTo(Access.PRIVATE);
            assertThat(courseRepository.findById(courseId).orElseThrow().getTags().size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("내 코스가 아닌 경우")
        void not_my_course() throws Exception {
            User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", UserStatus.LOGIN, Authority.USER));
            List<TagCreateRequest> updateTag = List.of(
                    new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME),
                    new TagCreateRequest(new PlaceCreateRequest(2L, "new place", 1.0, 1.0), "new tag")
            );
            assertThatThrownBy(() -> courseService.update(other.getId(), courseId, "updated", "updated", Access.PRIVATE, updateTag, null))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_PERMISSION.getMessage());
        }
    }
}
