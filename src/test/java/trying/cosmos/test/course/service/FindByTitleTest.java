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
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.dto.response.CourseFindContent;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.place.service.PlaceService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.course.entity.Access.PRIVATE;
import static trying.cosmos.domain.course.entity.Access.PUBLIC;
import static trying.cosmos.domain.planet.entity.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 제목으로 코스 조회")
public class FindByTitleTest {

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
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User me = userRepository.save(new User("me@gmail.com", PASSWORD, "me", LOGIN, USER));
        this.userId = me.getId();
        User other = userRepository.save(new User("other@gmail.com", PASSWORD, "other", LOGIN, USER));

        Planet myPlanet = planetRepository.save(new Planet(me, PLANET_NAME, EARTH));
        Planet othersPlanet = planetRepository.save(new Planet(other, PLANET_NAME, EARTH));
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));

        courseService.create(me.getId(), myPlanet.getId(), "myPrivate", "myPrivate", PRIVATE, tagRequest, null);
        courseService.create(me.getId(), myPlanet.getId(), "myPublic", "myPublic", PUBLIC, tagRequest, null);
        courseService.create(other.getId(), othersPlanet.getId(), "othersPrivate", "othersPrivate", PRIVATE, tagRequest, null);
        courseService.create(other.getId(), othersPlanet.getId(), "othersPublic", "othersPublic", PUBLIC, tagRequest, null);
        Course deleted = courseService.create(me.getId(), myPlanet.getId(), "deleted", "deleted", PUBLIC, tagRequest, null);
        courseService.delete(me.getId(), deleted.getId());
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("코스 조회시 다른 사람의 비공개 코스는 숨겨짐, 삭제된 코스는 보이지 않음")
        void other_private() throws Exception {
            List<String> titles = courseService.findByTitle(userId, "", pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("othersPublic", "myPublic", "myPrivate");
        }

        @Test
        @DisplayName("id가 null인 경우 비공개 코스는 숨겨짐, 삭제된 코스는 보이지 않음")
        void anonymous() throws Exception {
            List<String> titles = courseService.findByTitle(null, "", pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("othersPublic", "myPublic");
        }

        @Test
        @DisplayName("제목으로 코스 조회")
        void query() throws Exception {
            List<String> titles = courseService.findByTitle(userId, "my", pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("myPublic", "myPrivate");
        }
    }
}
