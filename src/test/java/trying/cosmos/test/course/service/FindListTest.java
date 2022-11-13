package trying.cosmos.test.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.response.CourseFindResponse;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseLike;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스 목록 조회")
public class FindListTest {

    @Autowired
    CourseService courseService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setup() {
        em.persist(place1);
    }
    
    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("likeonly 파라미터가 true면 좋아요한 코스만 반환한다.")
        void find_with_likeonly_true() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            Course courseTomorrow = courseRepository.save(new Course(planet, "FIND", LocalDate.now().plusDays(1)));
            Course courseTodayLike = courseRepository.save(new Course(planet, "FIND", LocalDate.now()));
            Course courseTomorrowLike = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));
            em.persist(new CourseLike(user, courseTodayLike));
            em.persist(new CourseLike(user, courseTomorrowLike));

            // WHEN
            Slice<CourseFindResponse> courseListResponse = courseService.findList(user.getId(), "", true, pageable);

            // THEN
            List<Long> courseIds = courseListResponse.getContent().stream()
                    .map(CourseFindResponse::getCourseId)
                    .collect(Collectors.toList());
            assertThat(courseIds)
                    .containsExactly(courseTomorrowLike.getId(), courseTodayLike.getId());
        }

        @Test
        @DisplayName("likeonly 파라미터가 false면 모든 코스를 반환한다.")
        void find_with_likeonly_false() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            Course courseTomorrow = courseRepository.save(new Course(planet, "FIND", LocalDate.now().plusDays(1)));
            Course courseTodayLike = courseRepository.save(new Course(planet, "FIND", LocalDate.now()));
            Course courseTomorrowLike = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));
            em.persist(new CourseLike(user, courseTodayLike));
            em.persist(new CourseLike(user, courseTomorrowLike));

            // WHEN
            Slice<CourseFindResponse> courseListResponse = courseService.findList(user.getId(), "", false, pageable);

            // THEN
            List<Long> courseIds = courseListResponse.getContent().stream()
                    .map(CourseFindResponse::getCourseId)
                    .collect(Collectors.toList());
            assertThat(courseIds)
                    .containsExactly(courseTomorrowLike.getId(), courseTodayLike.getId(), courseTomorrow.getId(), courseToday.getId());
        }

        @Test
        @DisplayName("query 파라미터가 존재하면 해당 문자열을 제목에 포함하는 코스를 반환한다.")
        void find_with_query() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            Course courseTomorrow = courseRepository.save(new Course(planet, "FIND", LocalDate.now().plusDays(1)));
            Course courseTodayLike = courseRepository.save(new Course(planet, "FIND", LocalDate.now()));
            Course courseTomorrowLike = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));
            em.persist(new CourseLike(user, courseTodayLike));
            em.persist(new CourseLike(user, courseTomorrowLike));

            // WHEN
            Slice<CourseFindResponse> courseListResponse = courseService.findList(user.getId(), "FIND", false, pageable);

            // THEN
            List<Long> courseIds = courseListResponse.getContent().stream()
                    .map(CourseFindResponse::getCourseId)
                    .collect(Collectors.toList());
            assertThat(courseIds)
                    .containsExactly(courseTodayLike.getId(), courseTomorrow.getId());
        }

        @Test
        @DisplayName("likeonly가 true고 query가 존재하면 두 조건을 모두 만족하는 코스를 반환한다")
        void find_with_query_and_likeonly() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            Course courseTomorrow = courseRepository.save(new Course(planet, "FIND", LocalDate.now().plusDays(1)));
            Course courseTodayLike = courseRepository.save(new Course(planet, "FIND", LocalDate.now()));
            Course courseTomorrowLike = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));
            em.persist(new CourseLike(user, courseTodayLike));
            em.persist(new CourseLike(user, courseTomorrowLike));

            // WHEN
            Slice<CourseFindResponse> courseListResponse = courseService.findList(user.getId(), "FIND", true, pageable);

            // THEN
            List<Long> courseIds = courseListResponse.getContent().stream()
                    .map(CourseFindResponse::getCourseId)
                    .collect(Collectors.toList());
            assertThat(courseIds)
                    .containsExactly(courseTodayLike.getId());
        }
    }
}
