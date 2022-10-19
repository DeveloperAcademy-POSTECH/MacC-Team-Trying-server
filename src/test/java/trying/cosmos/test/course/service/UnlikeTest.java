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
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.course.CourseLikeRepository;
import trying.cosmos.domain.course.CourseRepository;
import trying.cosmos.domain.course.CourseService;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.place.PlaceCreateRequest;
import trying.cosmos.domain.place.PlaceService;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.course.Access.PRIVATE;
import static trying.cosmos.domain.planet.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles
@DisplayName("(Course.Service) 코스 좋아요 취소")
public class UnlikeTest {

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

    @Autowired
    CourseLikeRepository courseLikeRepository;

    private Long userId;
    private Course course;
    private Course others;
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User me = userRepository.save(new User("me@gmail.com", PASSWORD, "me", LOGIN, USER));
        this.userId = me.getId();
        User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", LOGIN, USER));
        Planet myPlanet = planetRepository.save(new Planet(me, PLANET_NAME, EARTH));
        Planet othersPlanet = planetRepository.save(new Planet(other, PLANET_NAME, EARTH));
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));

        course = courseService.create(me.getId(), myPlanet.getId(), "myPrivate", "myPrivate", PRIVATE, tagRequest);
        courseService.like(userId, course.getId());
        others = courseService.create(other.getId(), othersPlanet.getId(), "others", "others", PRIVATE, tagRequest);
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 좋아요 취소")
        void like() throws Exception {
            courseService.unlike(userId, course.getId());
            assertThat(courseLikeRepository.existsByUserIdAndCourseId(userId, course.getId())).isFalse();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("좋아요 상태가 아닌 경우")
        void already_like() throws Exception {
            courseService.unlike(userId, course.getId());
            assertThatThrownBy(() -> courseService.unlike(userId, course.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }

        @Test
        @DisplayName("숨겨진 코스인 경우")
        void hidden() throws Exception {
            assertThatThrownBy(() -> courseService.unlike(userId, others.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }
    }
}
