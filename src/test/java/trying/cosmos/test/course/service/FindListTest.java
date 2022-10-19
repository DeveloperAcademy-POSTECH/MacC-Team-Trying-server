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
import trying.cosmos.domain.course.CourseRepository;
import trying.cosmos.domain.course.CourseService;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.course.response.CourseFindContent;
import trying.cosmos.domain.place.PlaceCreateRequest;
import trying.cosmos.domain.place.PlaceService;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.course.Access.PRIVATE;
import static trying.cosmos.domain.course.Access.PUBLIC;
import static trying.cosmos.domain.planet.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 코스 목록 조회")
public class FindListTest {

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
    private Course myPrivate;
    private Course myPublic;
    private Course othersPrivate;
    private Course othersPublic;
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User me = userRepository.save(new User("me@gmail.com", PASSWORD, "me", LOGIN, USER));
        this.userId = me.getId();
        User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", LOGIN, USER));
        Planet myPlanet = planetRepository.save(new Planet(me, PLANET_NAME, EARTH));
        Planet othersPlanet = planetRepository.save(new Planet(other, PLANET_NAME, EARTH));
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));

        myPrivate = courseService.create(me.getId(), myPlanet.getId(), "myPrivate", "myPrivate", PRIVATE, tagRequest);
        myPublic = courseService.create(me.getId(), myPlanet.getId(), "myPublic", "myPublic", PUBLIC, tagRequest);
        othersPrivate = courseService.create(other.getId(), othersPlanet.getId(), "othersPrivate", "othersPrivate", PRIVATE, tagRequest);
        othersPublic = courseService.create(other.getId(), othersPlanet.getId(), "othersPublic", "othersPublic", PUBLIC, tagRequest);
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 조회시 다른 사람의 비공개 코스는 숨겨짐")
        void other_private() throws Exception {
            List<String> titles = courseService.findCourses(userId, pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("myPrivate", "myPublic", "othersPublic");
        }

        @Test
        @DisplayName("id가 null인 경우 비공개 코스는 숨겨짐")
        void anonymous() throws Exception {
            List<String> titles = courseService.findCourses(null, pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("myPublic", "othersPublic");
        }
    }
}
